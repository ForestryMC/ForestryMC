/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.climatology.climate;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;

import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateState;
import forestry.api.climate.LogicInfo;
import forestry.api.climate.source.IClimateSource;
import forestry.api.climate.source.IClimateSourceProxy;
import forestry.core.climate.ClimateStateHelper;
import forestry.core.config.Config;

public abstract class ClimateSource<P extends IClimateSourceProxy> implements IClimateSource<P> {

	protected final P proxy;
	protected final float boundModifier;
	protected final ClimateSourceType sourceType;
	private final IClimateState sourceState;
	protected final float defaultChange;
	protected ClimateSourceMode temperatureMode;
	protected ClimateSourceMode humidityMode;
	protected boolean isActive;

	public ClimateSource(P proxy, float defaultChange, float boundModifier, ClimateSourceType sourceType) {
		this.proxy = proxy;
		this.defaultChange = defaultChange;
		this.boundModifier = boundModifier;
		this.sourceType = sourceType;
		this.temperatureMode = ClimateSourceMode.NONE;
		this.humidityMode = ClimateSourceMode.NONE;
		this.sourceState = ClimateStateHelper.INSTANCE.mutableZero();
	}

	@Override
	public P getProxy() {
		return proxy;
	}

	public void setHumidityMode(ClimateSourceMode humidityMode) {
		this.humidityMode = humidityMode;
	}

	public void setTemperatureMode(ClimateSourceMode temperatureMode) {
		this.temperatureMode = temperatureMode;
	}

	protected float getEnergyModifier(IClimateState state) {
		return (1.0F + state.getTemperature() + state.getHumidity()) * Config.habitatformerResourceModifier;
	}

	protected float getBoundModifier(ClimateType type) {
		return boundModifier;
	}

	protected float getChange(ClimateType type) {
		return defaultChange;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}


	//@Override
	//public final IClimateState work(IClimateLogic logic, IClimateState previousState, IClimateState targetState, IClimateState currentState) {
		/*//Create a new sourceState that is a copy of the current sourceState
		IClimateState newState = ClimateStateHelper.INSTANCE.create(getState(), true);
		IClimateState newChange = ClimateStateHelper.INSTANCE.mutableZero();
		//The biome sourceState so we can later use it if the sourceState is beyond the targeted sourceState or the source has not enough resources to work
		IClimateState defaultState = logic.getBiome();
		//Check if the sourceState of the source is above or beyond the targeted sourceState and which climate types it can modify so it reaches the targeted sourceState
		ClimateSourceType workType = getWorkType(currentState, targetState, false);
		//Get the opposite type of the parameter above so we can go back to the biome sourceState if the source has not enough resource to work
		ClimateSourceType oppositeType = getWorkType(currentState, defaultState, true);
		//Setup the source
		beforeWork();
		boolean hasReached = hasReachedTarget(previousState, targetState, defaultState);
		boolean work = canWork(newState, logic.getResourceModifier());
		//Test if the source can work and test if the owner has enough resources to work.
		if (!work && oppositeType != null) {
			isActive = false;
			onIdle();
			if (ClimateStateHelper.isZero(newState)) {
				return newChange;
			} else if (ClimateStateHelper.isNearZero(newState)) {
				setState(newChange);
				return newChange;
			} else if (ClimateStateHelper.isNearTarget(currentState, targetState)) {
				return newChange;
			}
			//If the sourceState is not already zero, remove one change sourceState from the sourceState.
			newChange.add(getChange(oppositeType, defaultState, currentState)).scale(-logic.getChangeModifier());
		} else if (workType != null) {
			newChange = getChange(workType, targetState, previousState).scale(logic.getChangeModifier());
			IClimateState changedState = newState.add(newChange);
			boolean couldWork = canWork(changedState, logic.getResourceModifier());
			//Test if the owner could work with the changed sourceState. If he can remove the resources for the changed sourceState, if not only remove the resources for the old sourceState.
			removeResources(couldWork ? changedState : newState, logic.getResourceModifier());
			if (!couldWork) {
				newChange = ClimateStateHelper.ZERO_STATE;
			}
		} else if (oppositeType != null) {
			//Remove the resources if the owner has enough resources and the sourceState is not the default sourceState.
			removeResources(newState, logic.getResourceModifier());
		}
		newState = newState.add(newChange);
		if (ClimateStateHelper.isZero(newState) || ClimateStateHelper.isNearZero(newState)) {
			newState = ClimateStateHelper.ZERO_STATE;
		}
		setState(newState);
		return newChange;*/
	//IClimateState defaultState = logic.getDefault();
	//beforeWork();
		/*IClimateState changeState = ClimateStateHelper.INSTANCE.mutableZero();
		if(sourceType.canChangeHumidity()){
			changeState.addHumidity(humidityMode == ClimateSourceMode.POSITIVE ? defaultChange : -defaultChange);
		}
		if(sourceType.canChangeTemperature()){
			changeState.addTemperature(temperatureMode == ClimateSourceMode.POSITIVE ? defaultChange : -defaultChange);
		}
		IClimateState current = changeState.scale(10.0F);
		sourceState.add(current);
		IClimateState cState = current.copy(true).add(defaultState);*/
	//IClimateState cState = defaultState.copy(true);
	//Log.error(cState.toString());
	//for(int i = 0;i < 40;i++) {
	//	IClimateState state = applyChange(cState, targetState, defaultState, ClimateType.TEMPERATURE, logic.getChangeModifier(), true, false);
	//	Log.error("i: " + i + ", " + state.toString());
	//}
	//Log.error(cState.toString());
	//Log.error(sourceState.toString());
	//sourceState.setTemperature(0.0F);
	//sourceState.setHumidity(0.0F);
	//return ClimateStateHelper.ZERO_STATE;
	//}

	public final void doWork(LogicInfo info) {
		boolean canWork = canWork(sourceState, info.resourceModifier);
		if (canWork) {
			for (ClimateType type : ClimateType.values()) {
				IClimateState simulatedState = applyChange(info, type, true, true);
				if (canWork(simulatedState, info.resourceModifier)) {
					applyChange(info, type, true, false);
				}
			}
			isActive = true;
			removeResources(sourceState, info.resourceModifier);
		} else {
			applyChange(info, ClimateType.HUMIDITY, false, false);
			applyChange(info, ClimateType.TEMPERATURE, false, false);
			isActive = false;
			onIdle();
		}
	}

	private IClimateState applyChange(LogicInfo info, ClimateType type, boolean positive, boolean simulated) {
		IClimateState target = info.targetedState;
		IClimateState defaultState = info.defaultState;
		IClimateState currentState = info.currentState;
		if (!sourceType.canChange(type)) {
			return ClimateStateHelper.ZERO_STATE;
		}
		if (positive) {
			if (hasReachedTarget(type, currentState, target, defaultState)) {
				//Nothing to do, target already reached
				return ClimateStateHelper.ZERO_STATE;
			}
		} else {
			if (ClimateStateHelper.isZero(type, sourceState) || ClimateStateHelper.isNearZero(type, sourceState)) {
				return ClimateStateHelper.ZERO_STATE;
			}
		}
		float change = getChange(type);
		//Change nothing if the change is smaller than zero and the current state already zero.
		if (change < 0 && ClimateStateHelper.isNearZero(type, currentState)) {
			return ClimateStateHelper.ZERO_STATE;
		}
		IClimateState changeState = ClimateStateHelper.mutableOf(0.0F, 0.0F).add(type, change).multiply(info.changeModifier);
		if (positive) {
			IClimateState newState = currentState.copy(true).add(changeState);
			if (hasReachedTarget(type, newState, target, defaultState)) {
				float diff = newState.getClimate(type) - target.getClimate(type);
				changeState.subtract(type, diff);
			}
			if (hasReachedTarget(type, newState, ClimateStateHelper.ZERO_STATE, defaultState)) {
				float diff = newState.getClimate(type);
				changeState.subtract(type, diff);
			}
		} else {
			IClimateState stateChange = sourceState.copy(true).subtract(changeState);
			if (hasReachedTarget(type, stateChange, ClimateStateHelper.ZERO_STATE, ClimateStateHelper.ZERO_STATE.add(type, change))) {
				changeState.add(stateChange);
			}
			changeState.multiply(-1);
		}
		if (!simulated) {
			sourceState.add(changeState);
			currentState.add(changeState);
		}
		return changeState;
	}

	protected IClimateState checkState(IClimateState state, IClimateState target, IClimateState defaultState) {
		if (ClimateStateHelper.isZero(state) || ClimateStateHelper.isNearZero(state)) {
			return ClimateStateHelper.ZERO_STATE;
		}
		if (hasReachedTarget(state, target, defaultState)) {
			return ClimateStateHelper.ZERO_STATE;
		}
		return state;
	}

	protected void onIdle() {

	}

	protected void beforeWork() {
	}

	/**
	 * Check if the source has enough resources to work.
	 */
	protected abstract boolean canWork(IClimateState currentState, float resourceModifier);

	protected abstract void removeResources(IClimateState currentState, float resourceModifier);

	@Nullable
	private ClimateSourceType getWorkType(IClimateState state, IClimateState target, boolean opposite) {
		boolean canChangeHumidity = /*sourceType.canChangeHumidity() && */canChange(state.getHumidity(), target.getHumidity(), opposite ? humidityMode.getOpposite() : humidityMode);
		boolean canChangeTemperature = /*sourceType.canChangeHumidity() &&*/canChange(state.getTemperature(), target.getTemperature(), opposite ? temperatureMode.getOpposite() : temperatureMode);
		if (sourceType.canChangeHumidity()) {
			if (sourceType.canChangeHumidity()) {
				if (!opposite && canChangeHumidity && canChangeTemperature || opposite && (canChangeHumidity || canChangeTemperature)) {
					return ClimateSourceType.BOTH;
				} else {
					return null;
				}
			} else {
				return canChangeHumidity ? ClimateSourceType.HUMIDITY : null;
			}
		} else {
			if (sourceType.canChangeTemperature()) {
				return canChangeTemperature ? ClimateSourceType.TEMPERATURE : null;
			}
			return null;
		}
		/*if (canChangeHumidity) {
			return canChangeTemperature ? ClimateSourceType.BOTH : ClimateSourceType.HUMIDITY;
		} else {
			return canChangeTemperature ? ClimateSourceType.TEMPERATURE : null;
		}*/
	}

	/**
	 * Checks if the given sourceState is has reached the targeted sourceState.
	 *
	 * @param state        The climate sourceState that will be checked.
	 * @param target       The targeted sourceState of the habitatformer.
	 * @param defaultState The climate sourceState of the biome that is at the position of the habitatformer.
	 */
	private boolean hasReachedTarget(IClimateState state, IClimateState target, IClimateState defaultState) {
		return hasReachedTarget(ClimateType.TEMPERATURE, state, target, defaultState)
			|| hasReachedTarget(ClimateType.HUMIDITY, state, target, defaultState);
	}

	/**
	 * Checks if the given sourceState is has reached the targeted sourceState.
	 *
	 * @param state        The climate sourceState that will be checked.
	 * @param target       The targeted sourceState of the habitatformer.
	 * @param defaultState The climate sourceState of the biome that is at the position of the habitatformer.
	 */
	private boolean hasReachedTarget(ClimateType type, IClimateState state, IClimateState target, IClimateState defaultState) {
		IClimateState differenceTarget = target.copy(true).subtract(defaultState);
		float targetedValue = target.getClimate(type);
		float value = state.getClimate(type);
		return sourceType.canChange(type) && (differenceTarget.getClimate(type) >= 0 ? (value >= targetedValue) : (value <= targetedValue));
	}

	private boolean canChange(float value, float target, ClimateSourceMode mode) {
		return mode == ClimateSourceMode.POSITIVE && value < target || mode == ClimateSourceMode.NEGATIVE && value > target;
	}

	@Override
	public IClimateState getState() {
		return sourceState.copy();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		NBTTagCompound sourceData = new NBTTagCompound();
		//sourceState.writeToNBT(sourceData);
		nbt.setTag("Source", sourceData);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagCompound sourceData = nbt.getCompoundTag("Source");
		if (sourceData.hasNoTags()) {
			return;
		}
		//sourceState = ClimateStateHelper.INSTANCE.create(sourceData);
	}
}
