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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import forestry.api.climate.ClimateManager;
import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateHousing;
import forestry.api.climate.IClimateManipulatorBuilder;
import forestry.api.climate.IClimateState;
import forestry.api.climate.IClimateTransformer;
import forestry.api.climate.IWorldClimateHolder;
import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.core.config.Config;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;

public class ClimateTransformer implements IClimateTransformer, IStreamable, INbtReadable, INbtWritable {

	private static final String CURRENT_STATE_KEY = "Current";

	private static final String STATE_KEY = "State";
	private static final String TARGETED_STATE_KEY = "Target";
	private static final String CIRCULAR_KEY = "Circular";
	private static final String RANGE_KEY = "Range";

	//The tile entity that provides this logic.
	protected final IClimateHousing housing;
	//The state that this logic targets to reach.
	private IClimateState targetedState;
	//The current climate state of this logic.
	private IClimateState currentState;
	//The climate state of the biome that is located at the position of this tile.
	private IClimateState defaultState;
	//The range of the habitatformer in blocks in one direction.
	private int range;
	//The area of the former in blocks.
	private int area;
	//True if 'update()' was called at least once.
	private boolean addedToWorld;
	//True if the area of the former is circular.
	private boolean circular;

	public ClimateTransformer(IClimateHousing housing) {
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
			World world = housing.getWorldObj();
			BlockPos pos = housing.getCoordinates();
			defaultState = ClimateRoot.getInstance().getBiomeState(world, pos);
			if (!targetedState.isPresent()) {
				setCurrent(defaultState.copy());
				setTarget(defaultState);
			}
			IWorldClimateHolder worldClimate = ClimateManager.climateRoot.getWorldClimate(world);
			worldClimate.updateTransformer(this);
			addedToWorld = true;
		}
	}

	/* Climate Holders */
	@Override
	public void removeTransformer() {
		addedToWorld = false;
		IWorldClimateHolder worldClimate = ClimateManager.climateRoot.getWorldClimate(housing.getWorldObj());
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
		onAreaChange(range, circular);
	}

	@Override
	public IClimateManipulatorBuilder createManipulator(ClimateType type) {
		return new ClimateManipulator.Builder()
			.setDefault(defaultState)
			.setCurrent(currentState)
			.setTarget(targetedState)
			.setChangeSupplier(housing::getChangeForState)
			.setType(type)
			.setOnFinish(this::setCurrent);
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		data.writeClimateState(currentState);
		data.writeClimateState(targetedState);
		data.writeClimateState(defaultState);
		data.writeBoolean(circular);
		data.writeVarInt(range);
		onAreaChange(range, circular);
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
	public void setCurrent(IClimateState state) {
		state = ClimateStateHelper.INSTANCE.clamp(state.toImmutable());
		if (!state.equals(currentState)) {
			this.currentState = state;
			housing.markNetworkUpdate();
			if (addedToWorld) {
				IWorldClimateHolder worldClimate = ClimateManager.climateRoot.getWorldClimate(getWorldObj());
				worldClimate.updateTransformer(this);
			}
		}
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

	public void setCircular(boolean value) {
		if (this.circular != value) {
			this.circular = value;
			onAreaChange(range, !value);
			housing.markNetworkUpdate();
			if (addedToWorld) {
				IWorldClimateHolder worldClimate = ClimateManager.climateRoot.getWorldClimate(getWorldObj());
				worldClimate.updateTransformer(this);
			}
		}
	}

	@Override
	public boolean isCircular() {
		return circular;
	}

	@Override
	public void setRange(int value) {
		if (value != range) {
			int oldRange = range;
			this.range = MathHelper.clamp(value, 1, 16);
			onAreaChange(oldRange, circular);
			housing.markNetworkUpdate();
			if (addedToWorld) {
				IWorldClimateHolder worldClimate = ClimateManager.climateRoot.getWorldClimate(getWorldObj());
				worldClimate.updateTransformer(this);
			}
		}
	}

	private void onAreaChange(int range, boolean circular) {
		int prevArea = area;
		this.area = computeArea(range, circular);
		if (addedToWorld && area != prevArea) {
			int areaDelta = Math.abs(area - prevArea);
			float speedDelta = calculateSpeedModifier(areaDelta);
			IClimateState deltaState = currentState.subtract(defaultState);
			IClimateState scaledDelta = deltaState.multiply(area > prevArea ? (1.0F / speedDelta) : speedDelta);
			setCurrent(scaledDelta.add(defaultState));
		}
	}

	private static int computeArea(int range, boolean circular) {
		return circular ? Math.round((range + 0.5F) * (range + 0.5F) * 2.0F * (float) Math.PI) : (range * 2 + 1) * (range * 2 + 1);
	}

	@Override
	public float getAreaModifier() {
		return calculateAreaModifier(area);
	}

	@Override
	public float getCostModifier() {
		return 1.0F + (getAreaModifier() * Config.habitatformerAreaCostModifier);
	}

	@Override
	public float getSpeedModifier() {
		return calculateSpeedModifier(area);
	}

	private static float calculateSpeedModifier(float area) {
		return 1.0F + (calculateAreaModifier(area) * Config.habitatformerAreaSpeedModifier);
	}

	private static float calculateAreaModifier(float area) {
		return area / 36.0F;
	}

	@Override
	public int getArea() {
		return area;
	}

	@Override
	public int getRange() {
		return range;
	}

	@Override
	public BlockPos getCoordinates() {
		return housing.getCoordinates();
	}

	@Override
	public World getWorldObj() {
		return housing.getWorldObj();
	}
}
