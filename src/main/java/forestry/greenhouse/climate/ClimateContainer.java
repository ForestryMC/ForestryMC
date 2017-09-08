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

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.ClimateStateType;
import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateState;
import forestry.api.core.ForestryAPI;
import forestry.api.greenhouse.IClimateHousing;
import forestry.core.climate.AbsentClimateState;
import forestry.core.climate.ClimateStates;
import forestry.core.config.Config;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.packets.PacketUpdateClimate;
import forestry.core.utils.NetworkUtil;
import forestry.greenhouse.api.climate.IClimateContainer;
import forestry.greenhouse.api.climate.IClimateModifier;
import forestry.greenhouse.api.climate.IClimateSource;

public class ClimateContainer implements IClimateContainer, IStreamable {
	
	protected final IClimateHousing parent;
	protected final Set<IClimateSource> sources;
	protected final Supplier<Boolean> canWork;
	private int delay;
	protected IClimateState state;
	protected IClimateState targetedState;
	protected IClimateState boundaryUp;
	protected IClimateState boundaryDown;
	protected double sizeModifier;
	private NBTTagCompound modifierData;
	
	/**
	 * Creates an empty region.
	 */
	public ClimateContainer(IClimateHousing parent) {
		this(parent, () -> true);
	}

	public ClimateContainer(IClimateHousing parent, Supplier<Boolean> canWork) {
		this.parent = parent;
		this.sources = new HashSet<>();
		this.delay = 20;
		this.state = parent.getDefaultClimate().copy();
		this.modifierData = new NBTTagCompound();
		this.boundaryUp = ClimateStates.INSTANCE.min();
		this.boundaryDown = ClimateStates.INSTANCE.min();
		this.targetedState = AbsentClimateState.INSTANCE;
		this.canWork = canWork;
		this.sizeModifier = 1.0D;
	}
	
	@Override
	public IClimateHousing getParent() {
		return parent;
	}

	@Override
	public void updateClimate(int ticks) {
		if (ticks % getTickDelay() == 0) {
			IClimateState oldState = state.copy(ClimateStateType.DEFAULT);
			state = parent.getDefaultClimate().copy(ClimateStateType.EXTENDED);
			for (IClimateModifier modifier : GreenhouseClimateManager.getInstance().getModifiers()) {
				state = modifier.modifyTarget(this, state, oldState, modifierData).copy(ClimateStateType.EXTENDED);
			}
			if (!state.equals(oldState)) {
				BlockPos coordinates = parent.getCoordinates();
				NetworkUtil.sendNetworkPacket(new PacketUpdateClimate(coordinates, this), coordinates, parent.getWorldObj());
			}
		}
	}

	@Override
	public double getSizeModifier() {
		return sizeModifier;
	}

	@Override
	public void recalculateBoundaries() {
		sizeModifier = Math.max(parent.getSize() / Config.climateSourceRange, 1.0D);
		float temperatureBoundaryUp = 0.0F;
		float humidityBoundaryUp = 0.0F;
		float temperatureBoundaryDown = 0.0F;
		float humidityBoundaryDown = 0.0F;
		for(IClimateSource source : sources){
			if(source.affectClimateType(ClimateType.HUMIDITY)){
				humidityBoundaryUp+=source.getBoundaryModifier(ClimateType.HUMIDITY, true);
				humidityBoundaryDown+=source.getBoundaryModifier(ClimateType.HUMIDITY, false);
			}
			if(source.affectClimateType(ClimateType.TEMPERATURE)){
				temperatureBoundaryUp+= source.getBoundaryModifier(ClimateType.TEMPERATURE, true);
				temperatureBoundaryDown+= source.getBoundaryModifier(ClimateType.TEMPERATURE, false);
			}
		}
		if(temperatureBoundaryUp != 0){
			temperatureBoundaryUp/=sizeModifier;
		}
		if(temperatureBoundaryDown != 0){
			temperatureBoundaryDown/=sizeModifier;
		}
		if(humidityBoundaryUp != 0){
			humidityBoundaryUp/=sizeModifier;
		}
		if(humidityBoundaryDown != 0){
			humidityBoundaryDown/=sizeModifier;
		}
		boundaryUp = parent.getDefaultClimate().add(ClimateStates.of(temperatureBoundaryUp, humidityBoundaryUp));
		boundaryDown = parent.getDefaultClimate().remove(ClimateStates.of(temperatureBoundaryDown, humidityBoundaryDown));
	}
	
	@Override
	public IClimateState getBoundaryDown() {
		return boundaryDown;
	}
	
	@Override
	public IClimateState getBoundaryUp() {
		return boundaryUp;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		state.writeToNBT(nbt);
		nbt.setTag("Target", targetedState.writeToNBT(new NBTTagCompound()));
		nbt.setTag("modifierData", modifierData);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		state = ForestryAPI.states.create(nbt);
		targetedState = ForestryAPI.states.create(nbt.getCompoundTag("Target"));
		modifierData = nbt.getCompoundTag("modifierData");
	}
	
	@Override
	public void setTargetedState(IClimateState state) {
		this.targetedState = state;
	}
	
	@Override
	public IClimateState getTargetedState() {
		return targetedState;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	/**
	 * @return The ticks between updates.
	 */
	public int getTickDelay() {
		return delay;
	}

	public void setState(IClimateState state) {
		this.state = state;
	}

	@Override
	public World getWorld() {
		return parent.getWorldObj();
	}

	@Override
	public void addClimateSource(IClimateSource source) {
		if (!sources.contains(source)) {
			sources.add(source);
		}
	}

	@Override
	public void removeClimateSource(IClimateSource source) {
		if (sources.contains(source)) {
			sources.remove(source);
		}
	}

	@Override
	public Collection<IClimateSource> getClimateSources() {
		return sources;
	}
	
	@Override
	public void writeData(PacketBufferForestry data) {
		data.writeClimateState(state);
		data.writeClimateState(boundaryUp);
		data.writeClimateState(boundaryDown);
		data.writeClimateState(targetedState);
		data.writeCompoundTag(modifierData);
	}

	@Override
	public void readData(PacketBufferForestry data) throws IOException {
		state = data.readClimateState();
		boundaryUp = data.readClimateState();
		boundaryDown = data.readClimateState();
		targetedState = data.readClimateState();
		modifierData = data.readCompoundTag();
	}
	
	@Override
	public IClimateState getState() {
		return state;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof IClimateContainer)){
			return false;
		}
		IClimateContainer container = (IClimateContainer) obj;
		IClimateHousing parent = container.getParent();
		if(parent.getCoordinates() == null || this.parent.getCoordinates() == null){
			return false;
		}
		return this.parent.getCoordinates().equals(parent.getCoordinates());
	}
	
	@Override
	public int hashCode() {
		return parent.getCoordinates().hashCode();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addModifierInformation(IClimateModifier modifier, ClimateType type, List<String> lines) {
		if (!modifier.canModify(type)) {
			return;
		}
		modifier.addInformation(this, modifierData, type, lines);
	}

	@Override
	public boolean canWork() {
		return canWork.get();
	}

}
