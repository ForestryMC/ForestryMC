package forestry.core.climate;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import net.minecraft.util.math.MathHelper;

import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateManipulator;
import forestry.api.climate.IClimateManipulatorBuilder;
import forestry.api.climate.IClimateState;

public class ClimateManipulator implements IClimateManipulator {
	private final IClimateState targetedState;
	private final IClimateState defaultState;
	private final IClimateState currentState;
	private final IClimateState startState;
	private final BiFunction<ClimateType, IClimateManipulator, Float> changeSupplier;
	private final Consumer<IClimateState> onFinish;
	private final boolean backwards;
	private final ClimateType type;

	private ClimateManipulator(IClimateState targetedState, IClimateState defaultState, IClimateState currentState, BiFunction<ClimateType, IClimateManipulator, Float> changeSupplier, Consumer<IClimateState> onFinish, boolean backwards, ClimateType type) {
		IClimateState current = currentState.isPresent() ? currentState : defaultState;
		this.targetedState = targetedState;
		this.defaultState = defaultState;
		this.currentState = current.copy(true);
		this.startState = current;
		this.changeSupplier = changeSupplier;
		this.onFinish = onFinish;
		this.backwards = backwards;
		this.type = type;
	}

	@Override
	public IClimateState getCurrent() {
		return currentState;
	}

	@Override
	public IClimateState getStart() {
		return startState;
	}

	@Override
	public IClimateState getTarget() {
		return targetedState;
	}

	@Override
	public boolean allowsBackwards() {
		return backwards;
	}

	@Override
	public IClimateState getDefault() {
		return defaultState;
	}

	@Override
	public ClimateType getType() {
		return type;
	}

	@Override
	public void finish() {
		if (!currentState.equals(startState)) {
			onFinish.accept(currentState);
		}
	}

	@Override
	public IClimateState addChange(boolean simulated) {
		return applyChange(true, simulated);
	}

	@Override
	public IClimateState removeChange(boolean simulated) {
		return applyChange(false, simulated);
	}

	@Override
	public boolean canAdd() {
		//Difference between the targeted state of this method and the current state.
		IClimateState difference = targetedState.subtract(startState);
		if (ClimateStateHelper.isZero(type, difference)) {
			return true;
		}
		float change = changeSupplier.apply(type, this);
		boolean rightDirection = difference.getClimate(type) > 0.0F && change > 0.0F || difference.getClimate(type) < 0.0F && change < 0.0F;
		if (!rightDirection) {
			IClimateState diffToDefault = startState.subtract(defaultState);
			return backwards || (diffToDefault.getClimate(type) > 0.0F && change > 0.0F || diffToDefault.getClimate(type) < 0.0F && change < 0.0F);
		}
		return true;
	}

	/**
	 * Applies the current return value of 'change' to the current climate value of the
	 * given type. Negates the change if the machine had not enough resources to work
	 *
	 * @param worked    If the machine had enough resources to work and can hold the current climate state.
	 * @param simulated If the action should only been simulated.
	 * @return The change that the method would have or has applied to the current state.
	 */
	private IClimateState applyChange(boolean worked, boolean simulated) {
		IClimateState target = worked ? targetedState : defaultState;
		//Difference between the targeted state of this method and the current state.
		IClimateState difference = target.subtract(startState);
		//Do nothing if the current state already equals the targeted state of this method.
		if (ClimateStateHelper.isZero(type, difference)) {
			return ClimateStateHelper.ZERO_STATE;
		}
		float change = changeSupplier.apply(type, this);
		//Create a mutable state that contains the current change.
		IClimateState changeState = ClimateStateHelper.INSTANCE.create(type, change).toMutable();
		boolean rightDirection = difference.getClimate(type) > 0.0F && change > 0.0F || difference.getClimate(type) < 0.0F && change < 0.0F;
		if (!rightDirection) {
			IClimateState diffToDefault = startState.subtract(defaultState);
			//Check if 'bothDirections' is true or if the difference to the default state has the same direction like the change state.
			//The Second one allows to go back to the default state if the current target is above, if the change is negative, or below, if the change is positive, the last targeted state.
			if (!worked || backwards || (diffToDefault.getClimate(type) > 0.0F && change > 0.0F || diffToDefault.getClimate(type) < 0.0F && change < 0.0F)) {
				//If so negate the current change so we can go back the targeted state.
				changeState.multiply(-1.0F);
			} else {
				//If not change nothing because we are not allowed to.
				return ClimateStateHelper.ZERO_STATE;
			}
		}
		IClimateState newState = startState.add(changeState);
		IClimateState newDifference = target.subtract(newState);
		float diff = newDifference.getClimate(type);
		//Round up or down to the targeted state if possible
		if (canRound(diff)) {
			changeState.add(type, diff);
		}
		//Add the change state to the current state if this isn't simulated
		if (!simulated) {
			currentState.add(changeState);
		}
		return changeState;
	}

	private static boolean canRound(float diff) {
		return BigDecimal.valueOf(MathHelper.abs(diff)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() <= ClimateStateHelper.CLIMATE_CHANGE;
	}

	public static class Builder implements IClimateManipulatorBuilder {
		@Nullable
		private IClimateState targetedState = null;
		@Nullable
		private IClimateState defaultState = null;
		@Nullable
		private IClimateState currentState = null;
		@Nullable
		private BiFunction<ClimateType, IClimateManipulator, Float> changeSupplier = null;
		@Nullable
		private ClimateType type = null;
		private Consumer<IClimateState> onFinish = climateState -> {
		};
		private boolean backwards = false;

		public IClimateManipulatorBuilder setType(ClimateType type) {
			this.type = type;
			return this;
		}

		@Override
		public IClimateManipulatorBuilder setAllowBackwards() {
			this.backwards = true;
			return this;
		}

		@Override
		public IClimateManipulatorBuilder setChangeSupplier(BiFunction<ClimateType, IClimateManipulator, Float> changeSupplier) {
			this.changeSupplier = changeSupplier;
			return this;
		}

		@Override
		public IClimateManipulatorBuilder setOnFinish(Consumer<IClimateState> onFinish) {
			this.onFinish = onFinish;
			return this;
		}

		@Override
		public IClimateManipulatorBuilder setTarget(IClimateState state) {
			this.targetedState = state.toImmutable();
			return this;
		}

		@Override
		public IClimateManipulatorBuilder setDefault(IClimateState state) {
			this.defaultState = state.toImmutable();
			return this;
		}

		@Override
		public IClimateManipulatorBuilder setCurrent(IClimateState state) {
			this.currentState = state.toImmutable();
			return this;
		}

		@Override
		public IClimateManipulator build() {
			Preconditions.checkNotNull(targetedState);
			Preconditions.checkNotNull(defaultState);
			Preconditions.checkNotNull(currentState);
			Preconditions.checkNotNull(changeSupplier);
			Preconditions.checkNotNull(type);
			return new ClimateManipulator(targetedState, defaultState, currentState, changeSupplier, onFinish, backwards, type);
		}
	}
}
