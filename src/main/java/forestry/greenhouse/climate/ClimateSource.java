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
package forestry.greenhouse.climate;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import forestry.api.climate.ClimateStateType;
import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateState;
import forestry.core.climate.ClimateStates;
import forestry.core.utils.Log;
import forestry.greenhouse.api.climate.IClimateContainer;
import forestry.greenhouse.api.climate.IClimateSource;
import forestry.greenhouse.api.climate.IClimateSourceOwner;
import forestry.greenhouse.api.greenhouse.GreenhouseManager;

public abstract class ClimateSource<O extends IClimateSourceOwner> implements IClimateSource {

	protected final float range;
	protected final ClimateSourceType sourceType;
	protected O owner;
	private IClimateState state;
	protected float change;
	protected ClimateSourceMode temperatureMode;
	protected ClimateSourceMode humidityMode;
	protected IClimateContainer container;
	protected boolean addedToManager;
	protected boolean isActive;

	public ClimateSource(float change, float range, ClimateSourceType sourceType) {
		this.change = change;
		this.range = range;
		this.sourceType = sourceType;
		this.temperatureMode = ClimateSourceMode.NONE;
		this.humidityMode = ClimateSourceMode.NONE;
		this.state = ClimateStates.INSTANCE.absent();
	}
	
	public void setHumidityMode(ClimateSourceMode humidityMode) {
		this.humidityMode = humidityMode;
	}
	
	public void setTemperatureMode(ClimateSourceMode temperatureMode) {
		this.temperatureMode = temperatureMode;
	}
	
	public void setOwner(O owner) {
		this.owner = owner;
	}
	
	@Override
	public float getBoundaryModifier(ClimateType type, boolean boundaryUp) {
		if(type == ClimateType.HUMIDITY){
			if(humidityMode == ClimateSourceMode.POSITIVE && boundaryUp){
				return getRange(ClimateType.HUMIDITY);
			}else if(humidityMode == ClimateSourceMode.NEGATIVE && !boundaryUp){
				return getRange(ClimateType.HUMIDITY);
			}
		}else {
			if(temperatureMode == ClimateSourceMode.POSITIVE && boundaryUp){
				return getRange(ClimateType.TEMPERATURE);
			}else if(temperatureMode == ClimateSourceMode.NEGATIVE && !boundaryUp){
				return getRange(ClimateType.TEMPERATURE);
			}
		}
		return 0;
	}
	
	protected float getRange(ClimateType type){
		return range;
	}
	
	protected float getChange(ClimateType type) {
		return change;
	}
	
	@Override
	public boolean isActive() {
		return isActive;
	}
	
	@Override
	public boolean affectClimateType(ClimateType type) {
		return sourceType.affectClimateType(type);
	}

	@Override
	public IClimateSourceOwner getOwner() {
		return owner;
	}
	
	@Override
	public void onAdded(IClimateContainer container) {
		this.container = container;
	}
	
	@Override
	public void onRemoved(IClimateContainer container) {
		this.container = null;
	}
	
	public void update() {
		if(!addedToManager){
			onLoad();
		}
	}
	
	public void onLoad() {
		World world = owner.getWorldObj();
		if(!addedToManager && !world.isRemote) {
			GreenhouseManager.climateManager.addSource(owner);
			addedToManager = true;
		}
		
	}
	
	public void invalidate() {
		this.onChunkUnload();
	}
	
	public void onChunkUnload() {
		World world = owner.getWorldObj();
		if(addedToManager &&!world.isRemote) {
			GreenhouseManager.climateManager.removeSource(owner);
			addedToManager = false;
		}
	}

	@Override
	public final IClimateState work(IClimateState previousState, IClimateState targetState, IClimateState currentState, final double sizeModifier, final boolean canWork) {
		IClimateState state = ClimateStates.INSTANCE.create(getState(), ClimateStateType.EXTENDED);
		IClimateState change = ClimateStates.extendedZero();
		IClimateState defaultState = container.getParent().getDefaultClimate();
		ClimateSourceType validType = getWorkType(currentState, targetState);
		ClimateSourceType oppositeType = getOppositeWorkType(currentState, defaultState);
		beforeWork();
		boolean work = canWork(state, oppositeType);
		//Test if the source can work and test if the owner has enough resources to work.
		if (!canWork || !work && oppositeType != null) {
			isActive = false;
			isNotValid();
			if (ClimateStates.isZero(state)) {
				return change;
			} else if (ClimateStates.isNearZero(state)) {
				setState(change);
				return change;
			} else if (ClimateStates.isNearTarget(currentState, targetState)) {
				return change;
			}
			//If the state is not already zero, remove one change state from the state.
			change = getChange(oppositeType, defaultState, currentState);
			change = ClimateStates.INSTANCE.create(-change.getTemperature(), -change.getHumidity(), ClimateStateType.EXTENDED);
		} else if (validType == null && oppositeType != null) {
			//Remove the resources if the owner has enough resources and the state is not the default state.
			removeResources(state, oppositeType);
		} else {
			change = getChange(validType, targetState, previousState);
			IClimateState changedState = state.add(change.scale(1 / sizeModifier));
			boolean couldWork = canWork(changedState, oppositeType);
			//Test if the owner could work with the changed state. If he can remove the resources for the changed state, if not only remove the resources for the old state.
			removeResources(couldWork ? changedState : state, oppositeType);
			if (!couldWork) {
				change = ClimateStates.extendedZero();
			}
		}
		state.add(change.scale(1 / sizeModifier));
		if (ClimateStates.isZero(state) || ClimateStates.isNearZero(state)) {
			state = ClimateStates.extendedZero();
		}
		if (false) {
			state = ClimateStates.extendedZero();
		}
		if (!state.isPresent()) {
			state = ClimateStates.extendedZero();
			Log.error("Failed to update a climate source. Please report this to the authors of the mod. Previous={}, Target={}, Current={}, Size={}", previousState, targetState, currentState, sizeModifier);
		}
		setState(state);
		return change;
	}

	protected void isNotValid(){
		
	}
	
	protected void beforeWork(){
	}
	
	/**
	 * @return true if the source can work, false if it can not.
	 */
	protected abstract boolean canWork(IClimateState currentState, ClimateSourceType oppositeType);

	protected abstract void removeResources(IClimateState currentState, ClimateSourceType oppositeType);

	protected abstract IClimateState getChange(@Nullable ClimateSourceType type, IClimateState target, IClimateState currentState);

	@Nullable
	protected ClimateSourceType getOppositeWorkType(IClimateState state, IClimateState target) {
		boolean canChangeHumidity = sourceType.canChangeHumidity() && canChange(state.getHumidity(), target.getHumidity(), humidityMode.getOpposite());
		boolean canChangeTemperature = sourceType.canChangeTemperature() && canChange(state.getTemperature(), target.getTemperature(), temperatureMode.getOpposite());
		return canChangeHumidity ? canChangeTemperature ? ClimateSourceType.BOTH : ClimateSourceType.HUMIDITY : canChangeTemperature ? ClimateSourceType.TEMPERATURE : null;
	}

	@Nullable
	protected ClimateSourceType getWorkType(IClimateState state, IClimateState target) {
		boolean canChangeHumidity = sourceType.canChangeHumidity() && canChange(state.getHumidity(), target.getHumidity(), humidityMode);
		boolean canChangeTemperature = sourceType.canChangeTemperature() && canChange(state.getTemperature(), target.getTemperature(), temperatureMode);
		return canChangeHumidity ? canChangeTemperature ? ClimateSourceType.BOTH : ClimateSourceType.HUMIDITY : canChangeTemperature ? ClimateSourceType.TEMPERATURE : null;
	}
	
	private boolean canChange(float value, float target, ClimateSourceMode mode){
		if(mode == ClimateSourceMode.POSITIVE && value < target){
			return true;
		}else if(mode == ClimateSourceMode.NEGATIVE && value > target){
			return true;
		}
		return false;
	}

	@Override
	public IClimateState getState() {
		return state;
	}

	protected void setState(IClimateState state) {
		this.state = state;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		NBTTagCompound sourceData = new NBTTagCompound();
		state.writeToNBT(sourceData);
		nbt.setTag("Source", sourceData);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagCompound sourceData = nbt.getCompoundTag("Source");
		if (sourceData.hasNoTags()) {
			return;
		}
		state = ClimateStates.INSTANCE.create(sourceData, ClimateStateType.EXTENDED);
	}

}
