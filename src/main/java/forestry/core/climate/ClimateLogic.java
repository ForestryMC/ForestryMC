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

import java.io.IOException;
import java.util.function.BiFunction;

import net.minecraft.nbt.NBTTagCompound;

import forestry.api.climate.ClimateManager;
import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateHousing;
import forestry.api.climate.IClimateLogic;
import forestry.api.climate.IClimateManipulator;
import forestry.api.climate.IClimateState;
import forestry.api.climate.IWorldClimateHolder;
import forestry.api.climate.LogicInfo;
import forestry.core.config.Config;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;

public class ClimateLogic implements IClimateLogic, IStreamable {

	private static final String CURRENT_STATE_KEY = "Current";
	private static final String TARGETED_STATE_KEY = "Target";
	private static final String CIRCULAR_KEY = "Circular";
	private static final String RANGE_KEY = "Range";

	//The tile entity that provides this logic.
	protected final IClimateHousing housing;
	//The state that this logic targets to reach.
	private IClimateState targetedState;
	//The current climate state of this logic.
	protected IClimateState currentState;
	//The climate state of the biome that is located at the position of this tile.
	private IClimateState defaultState;
	//The radius of the habitatformer in blocks
	private int range;
	private int area;
	private boolean addedToWorld;
	private boolean circular;
	private float changeChange = 1.0F;
	private float rangeChange = 1.0F;
	private float resourceChange = 1.0F;

	public ClimateLogic(IClimateHousing housing) {
		this.housing = housing;
		this.currentState = ClimateStateHelper.INSTANCE.absent();
		this.defaultState = AbsentClimateState.INSTANCE;
		this.targetedState = AbsentClimateState.INSTANCE;
		setRange(Config.habitatformerRange);
		this.circular = true;
		this.addedToWorld = false;
	}

	@Override
	public IClimateHousing getHousing() {
		return housing;
	}

	@Override
	public void update() {
		if (!addedToWorld) {
			defaultState = ClimateRoot.getInstance().getBiomeState(getWorldObj(), getCoordinates());
			if (!targetedState.isPresent()) {
				setState(defaultState.copy());
				setTarget(defaultState);
			}
			IWorldClimateHolder worldClimate = ClimateManager.climateRoot.getWorldClimate(getWorldObj());
			worldClimate.updateTransformer(this);
			addedToWorld = true;
		}
	}

	/* Climate Holders */
	@Override
	public void onRemoval() {
		addedToWorld = false;
		IWorldClimateHolder worldClimate = ClimateManager.climateRoot.getWorldClimate(getWorldObj());
		worldClimate.removeTransformer(this);
	}

	/* Save and Load */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setTag(CURRENT_STATE_KEY, ClimateStateHelper.INSTANCE.writeToNBT(new NBTTagCompound(), currentState));
		nbt.setTag(TARGETED_STATE_KEY, ClimateStateHelper.INSTANCE.writeToNBT(new NBTTagCompound(), targetedState));
		nbt.setBoolean(CIRCULAR_KEY, circular);
		nbt.setInteger(RANGE_KEY, range);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		currentState = ClimateManager.stateHelper.create(nbt.getCompoundTag(CURRENT_STATE_KEY));
		targetedState = ClimateManager.stateHelper.create(nbt.getCompoundTag(TARGETED_STATE_KEY));
		circular = nbt.getBoolean(CIRCULAR_KEY);
		range = nbt.getInteger(RANGE_KEY);
	}

	@Override
	public LogicInfo createInfo() {
		return new LogicInfo(this, targetedState, defaultState, currentState, resourceChange, changeChange);
	}

	@Override
	public IClimateManipulator createManipulator(ClimateType type, BiFunction<ClimateType, LogicInfo, Float> changeSupplier) {
		return new ClimateManipulator(createInfo(), type, changeSupplier);
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		data.writeClimateState(currentState);
		data.writeClimateState(targetedState);
		data.writeClimateState(defaultState);
		data.writeBoolean(circular);
		data.writeVarInt(range);
	}

	@Override
	public void readData(PacketBufferForestry data) throws IOException {
		currentState = data.readClimateState();
		targetedState = data.readClimateState();
		defaultState = data.readClimateState();
		circular = data.readBoolean();
		range = data.readVarInt();
	}

	@Override
	public IClimateState getCurrent() {
		return currentState;
	}

	@Override
	public void setState(IClimateState state) {
		this.currentState = ClimateStateHelper.INSTANCE.clamp(state.toImmutable());
		housing.markNetworkUpdate();
	}

	@Override
	public IClimateState getTarget() {
		return targetedState;
	}

	@Override
	public void setTarget(IClimateState target) {
		this.targetedState = ClimateStateHelper.INSTANCE.clamp(target.toImmutable());
		housing.markNetworkUpdate();
	}

	@Override
	public IClimateState getDefault() {
		return defaultState;
	}

	public void setCircular(boolean circular) {
		this.circular = circular;
		housing.markNetworkUpdate();
	}

	public boolean isCircular() {
		return circular;
	}

	@Override
	public void setRange(int range) {
		this.range = range;
		this.addedToWorld = true;
		if(circular){
			this.area = Math.round(this.range * this.range * 2.0F * (float)Math.PI);
		}else{
			this.area = (range * 2 + 1) * (range * 2 + 1);
		}
		housing.markNetworkUpdate();
	}

	public int getArea() {
		return area;
	}

	@Override
	public int hashCode() {
		return housing.getCoordinates().hashCode();
	}

	@Override
	public int getRange() {
		return Math.round(range * getRangeModifier());
	}

	public float getResourceModifier(){
		return resourceChange;
	}

	public float getChangeModifier(){
		return changeChange;
	}

	public float getRangeModifier(){
		return rangeChange;
	}

	public void changeClimateConfig(float changeChange, float rangeChange, float energyChange) {
		this.changeChange+=changeChange;
		this.rangeChange+=rangeChange;
		this.resourceChange +=energyChange;
	}
}
