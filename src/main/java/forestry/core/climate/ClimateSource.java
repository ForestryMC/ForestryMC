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
package forestry.core.climate;

import javax.annotation.Nullable;

import net.minecraft.world.World;

import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateState;
import forestry.greenhouse.api.climate.IClimateContainer;
import forestry.greenhouse.api.climate.IClimateSource;
import forestry.greenhouse.api.climate.IClimateSourceOwner;
import forestry.greenhouse.api.greenhouse.GreenhouseManager;

public abstract class ClimateSource<O extends IClimateSourceOwner> implements IClimateSource {
	
	protected O owner;
	protected float change;
	protected final float range;
	protected final ClimateSourceType sourceType;
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
	public IClimateState work(IClimateState state, IClimateState target) {
		beforeWork();
		ClimateSourceType validType = getValidType(state, target);
		if(validType == null){
			isNotValid();
			isActive = false;
			return AbsentClimateState.INSTANCE;
		}
		if(!canWork(state, target)){
			isActive = false;
			return AbsentClimateState.INSTANCE;
		}
		removeResources(state, target);
		isActive = true;
		return getChange(validType, state, target);
	}
	
	protected void isNotValid(){
		
	}
	
	protected void beforeWork(){
	}
	
	/**
	 * @param state the {@link IClimateState} that the source has to work on.
	 * @param target the by the {@link IClimateContainer} targeted {@link IClimateState}.
	 * 
	 * @return true if the source can work, false if it can not.
	 */
	protected abstract boolean canWork(IClimateState state, IClimateState target);
	
	protected abstract void removeResources(IClimateState state, IClimateState target);
	
	protected abstract IClimateState getChange(ClimateSourceType type, IClimateState state, IClimateState target);
	
	@Nullable
	private ClimateSourceType getValidType(IClimateState state, IClimateState target){
		boolean canChangeHumidity = false;
		boolean canChangeTemperature = false;
		if(sourceType.canChangeHumidity()){
			if(canChange(state.getHumidity(), target.getHumidity(), humidityMode)){
				canChangeHumidity = true;
			}
		}
		if(sourceType.canChangeTemperature()){
			if(canChange(state.getTemperature(), target.getTemperature(), temperatureMode)){
				canChangeTemperature = true;
			}
		}
		if(canChangeHumidity){
			if(canChangeTemperature){
				return ClimateSourceType.BOTH;
			}
			return ClimateSourceType.HUMIDITY;
		}else if(canChangeTemperature){
			return ClimateSourceType.TEMPERATURE;
		}
		return null;
	}
	
	private boolean canChange(float value, float target, ClimateSourceMode mode){
		if(mode == ClimateSourceMode.POSITIVE && value < target){
			return true;
		}else if(mode == ClimateSourceMode.NEGATIVE && value > target){
			return true;
		}
		return false;
	}

}
