package forestry.core.climate;

import java.math.BigDecimal;
import java.util.function.BiFunction;

import net.minecraft.util.math.MathHelper;

import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateManipulator;
import forestry.api.climate.IClimateState;
import forestry.api.climate.TransformerProperties;

public class ClimateManipulator implements IClimateManipulator {
	private final TransformerProperties logic;
	private final ClimateType type;
	private final BiFunction<ClimateType, TransformerProperties, Float> changeSupplier;
	private boolean backwards = false;

	public ClimateManipulator(TransformerProperties logic, ClimateType type, BiFunction<ClimateType, TransformerProperties, Float> changeSupplier) {
		this.logic = logic;
		this.type = type;
		this.changeSupplier = changeSupplier;
	}

	@Override
	public void setAllowBackwards() {
		backwards = true;
	}

	@Override
	public void finish() {
		if(!logic.currentState.equals(logic.startState)){
			logic.parent.setCurrent(logic.currentState);
		}
	}

	@Override
	public IClimateState addChange( boolean simulated){
		return applyChange(true, simulated);
	}

	@Override
	public IClimateState removeChange(boolean simulated){
		return applyChange(false, simulated);
	}

	@Override
	public boolean canAdd(){
		IClimateState target = logic.targetedState;
		//Difference between the targeted state of this method and the current state.
		IClimateState difference = target.subtract(logic.startState);
		if(ClimateStateHelper.isZero(type, difference)){
			return true;
		}
		float change = changeSupplier.apply(type, logic);
		boolean rightDirection = difference.getClimate(type) > 0.0F && change > 0.0F || difference.getClimate(type) < 0.0F && change < 0.0F;
		if(!rightDirection){
			IClimateState diffToDefault = logic.startState.subtract(logic.defaultState);
			return backwards || (diffToDefault.getClimate(type) > 0.0F && change > 0.0F || diffToDefault.getClimate(type) < 0.0F && change < 0.0F);
		}
		return true;
	}

	/**
	 * Applies the current return value of 'change' to the current climate value of the
	 * given type. Negates the change if the machine had not enough resources to work
	 *
	 * @param worked If the machine had enough resources to work and can hold the current climate state.
	 * @param simulated If the action should only been simulated.
	 *
	 * @return The change that the method would have or has applied to the current state.
	 */
	private IClimateState applyChange(boolean worked, boolean simulated){
		IClimateState target = worked ? logic.targetedState : logic.defaultState;
		//Difference between the targeted state of this method and the current state.
		IClimateState difference = target.subtract(logic.startState);
		//Do nothing if the current state already equals the targeted state of this method.
		if(ClimateStateHelper.isZero(type, difference)){
			return ClimateStateHelper.ZERO_STATE;
		}
		float change = changeSupplier.apply(type, logic);
		//Create a mutable state that contains the current change.
		IClimateState changeState = ClimateStateHelper.INSTANCE.create(type, change).toMutable();
		boolean rightDirection = difference.getClimate(type) > 0.0F && change > 0.0F || difference.getClimate(type) < 0.0F && change < 0.0F;
		if(!rightDirection){
			IClimateState diffToDefault = logic.startState.subtract(logic.defaultState);
			//Check if 'bothDirections' is true or if the difference to the default state has the same direction like the change state.
			//The Second one allows to go back to the default state if the current target is above, if the change is negative, or below, if the change is positive, the last targeted state.
			if(!worked || backwards || (diffToDefault.getClimate(type) > 0.0F && change > 0.0F || diffToDefault.getClimate(type) < 0.0F && change < 0.0F)){
				//If so negate the current change so we can go back the targeted state.
				changeState.multiply(-1.0F);
			}else{
				//If not change nothing because we are not allowed to.
				return ClimateStateHelper.ZERO_STATE;
			}
		}
		IClimateState newState = logic.startState.add(changeState);
		IClimateState newDifference = target.subtract(newState);
		float diff = newDifference.getClimate(type);
		//Round up or down to the targeted state if possible
		if(canRound(diff)){
			changeState.add(type, diff);
		}
		//Add the change state to the current state if this isn't simulated
		if(!simulated) {
			logic.currentState.add(changeState);
		}
		return changeState;
	}

	private static boolean canRound(float diff){
		return BigDecimal.valueOf(MathHelper.abs(diff)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() <= ClimateStateHelper.CLIMATE_CHANGE;
	}
}
