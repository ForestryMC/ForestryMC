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
import forestry.api.climate.IClimateManipulator;
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

	private static final String BACKUP_KEY = "Backup";
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
	//A backup of the state before the last change of a area state (range or circular). Slowly moves back to the default state.
	//Is used if the player changes back to the old area state.
	private Backup backup = new Backup();
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
		backup.update();
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
		nbt.setTag(BACKUP_KEY, backup.writeToNBT(new NBTTagCompound()));
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		currentState = ClimateManager.stateHelper.create(nbt.getCompoundTag(CURRENT_STATE_KEY));
		targetedState = ClimateManager.stateHelper.create(nbt.getCompoundTag(TARGETED_STATE_KEY));
		circular = nbt.getBoolean(CIRCULAR_KEY);
		range = nbt.getInteger(RANGE_KEY);
		backup = new Backup(nbt.getCompoundTag(BACKUP_KEY));
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
		backup.writeData(data);
	}

	@Override
	public void readData(PacketBufferForestry data) throws IOException {
		currentState = data.readClimateState();
		targetedState = data.readClimateState();
		defaultState = data.readClimateState();
		circular = data.readBoolean();
		range = data.readVarInt();
		backup = new Backup(data);
	}

	@Override
	public IClimateState getCurrent() {
		return currentState;
	}

	@Override
	public void setCurrent(IClimateState state) {
		state = ClimateStateHelper.INSTANCE.clamp(state.toImmutable());
		if(!state.equals(currentState)) {
			this.currentState = state;
			housing.markNetworkUpdate();
			if(getWorldObj() != null) {
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

	public void setCircular(boolean circular) {
		if(this.circular != circular) {
			this.circular = circular;
			housing.markNetworkUpdate();
			if (getWorldObj() != null) {
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
		if(value != range) {
			if(!backup.apply(value, circular)){
				backup = new Backup(currentState, range, circular);
			}
			this.range = MathHelper.clamp(value, 1, 16);
			if (circular) {
				this.area = Math.round(this.range * this.range * 2.0F * (float) Math.PI);
			} else {
				this.area = (range * 2 + 1) * (range * 2 + 1);
			}
			housing.markNetworkUpdate();
			if (getWorldObj() != null) {
				IWorldClimateHolder worldClimate = ClimateManager.climateRoot.getWorldClimate(getWorldObj());
				worldClimate.updateTransformer(this);
			}
		}
	}

	@Override
	public float getAreaModifier(){
		return area / 36F;
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

	private class Backup implements INbtWritable{
		private final IClimateState state;
		private final int range;
		private final boolean circular;

		public Backup(IClimateState state, int range, boolean circular) {
			this.state = state.copy(true);
			this.range = range;
			this.circular = circular;
		}

		public Backup(NBTTagCompound compound) {
			state = ClimateStateHelper.INSTANCE.create(compound.getCompoundTag(STATE_KEY), true);
			range = compound.getInteger(RANGE_KEY);
			circular = compound.getBoolean(CIRCULAR_KEY);
		}

		public Backup(PacketBufferForestry data) {
			state = data.readClimateState();
			range = data.readVarInt();
			circular = data.readBoolean();
		}

		public Backup() {
			state = ClimateStateHelper.INSTANCE.absent();
			range = -1;
			circular = false;
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			nbt.setTag(STATE_KEY, ClimateStateHelper.INSTANCE.writeToNBT(new NBTTagCompound(), state));
			nbt.setInteger(RANGE_KEY, range);
			nbt.setBoolean(CIRCULAR_KEY, circular);
			return nbt;
		}

		public void writeData(PacketBufferForestry data) {
			data.writeClimateState(state);
			data.writeVarInt(range);
			data.writeBoolean(circular);
		}

		public boolean apply(int range, boolean circular){
			if(!state.isPresent()){
				return false;
			}
			if(range > this.range || !this.circular && circular){
				backup = new Backup(state, range, circular);
			}
			if(range == this.range && circular == this.circular) {
				setCurrent(state);
			}
			return true;
		}

		public void update(){
			if(!state.isPresent()){
				return;
			}
			for(ClimateType type : ClimateType.values()) {
				IClimateManipulator manipulator = createManipulator(type)
					.setCurrent(state)
					.setOnFinish(climateState -> state.setClimate(type, climateState.getClimate(type)))
					.setAllowBackwards().build();
				manipulator.removeChange(false);
				manipulator.finish();
			}
		}
	}
}
