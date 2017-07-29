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
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.ClimateState;
import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateContainer;
import forestry.api.climate.IClimateContainerListener;
import forestry.api.climate.IClimateHousing;
import forestry.api.climate.IClimateModifier;
import forestry.api.climate.IClimateSource;
import forestry.api.climate.IClimateState;
import forestry.api.climate.IClimateTable;
import forestry.api.climate.IClimateTableHelper;
import forestry.api.climate.ImmutableClimateState;
import forestry.api.greenhouse.GreenhouseManager;
import forestry.core.gui.tables.Table;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.packets.PacketUpdateClimate;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.Translator;

public class ClimateContainer implements IClimateContainer, IStreamable {
	public static final float CLIMATE_CHANGE = 0.01F;
	
	protected final IClimateHousing parent;
	protected final ClimateContainerListeners listeners;
	protected final Set<IClimateSource> sources;
	private int delay;
	protected ClimateState state;
	protected ImmutableClimateState targetedState;
	protected IClimateState boundaryUp;
	protected IClimateState boundaryDown;
	private NBTTagCompound modifierData;
	
	/**
	 * Creates an empty region.
	 */
	public ClimateContainer(IClimateHousing parent) {
		this.parent = parent;
		this.listeners = new ClimateContainerListeners();
		this.sources = new HashSet<>();
		this.delay = 20;
		this.targetedState = parent.getDefaultClimate();
		this.state = parent.getDefaultClimate().toMutable();
		this.modifierData = new NBTTagCompound();
		this.boundaryUp = ImmutableClimateState.MIN;
		this.boundaryDown = ImmutableClimateState.MIN;
	}

	public ClimateContainer(IClimateHousing parent, NBTTagCompound nbtTag) {
		this(parent);
		readFromNBT(nbtTag);
	}
	
	@Override
	public IClimateHousing getParent() {
		return parent;
	}

	@Override
	public void updateClimate(int ticks) {
		if (ticks % getTickDelay() == 0) {
			if(!listeners.isClosed(this)) {
				returnClimateToDefault();
			}else{
				ImmutableClimateState oldState = state.toImmutable();
				state = parent.getDefaultClimate().toMutable();
				for(IClimateModifier modifier : GreenhouseManager.greenhouseHelper.getModifiers()){
					state = modifier.modifyTarget(this, state, oldState, modifierData).toMutable();
				}
				if(!state.equals(oldState)) {
					BlockPos coordinates = parent.getCoordinates();
					NetworkUtil.sendNetworkPacket(new PacketUpdateClimate(coordinates, this), coordinates, parent.getWorldObj());
				}
			}
		}
	}
	
	protected void returnClimateToDefault(){
		ImmutableClimateState defaultState = parent.getDefaultClimate();
		float biomeTemperature = defaultState.getTemperature();
		float biomeHumidity = defaultState.getHumidity();
		float temperature = state.getTemperature();
		float humidity = state.getHumidity();
		if (temperature != biomeTemperature) {
			if (temperature > biomeTemperature) {
				state.addTemperature(-Math.min(CLIMATE_CHANGE, temperature - biomeTemperature));
			} else {
				state.addTemperature(Math.min(CLIMATE_CHANGE, biomeTemperature - temperature));
			}
		}
		if (humidity != biomeHumidity) {
			if (humidity > biomeHumidity) {
				state.addHumidity(-Math.min(CLIMATE_CHANGE, humidity - biomeHumidity));
			} else {
				state.addHumidity(Math.min(CLIMATE_CHANGE, biomeHumidity - humidity));
			}
		}
	}
	
	@Override
	public void recalculateBoundaries(double sizeModifier){
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
		boundaryUp = parent.getDefaultClimate().add(new ClimateState(temperatureBoundaryUp, humidityBoundaryUp));
		boundaryDown = parent.getDefaultClimate().remove(new ClimateState(temperatureBoundaryDown, humidityBoundaryDown));
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
		state.readFromNBT(nbt);
		targetedState = new ImmutableClimateState(nbt.getCompoundTag("Target"));
		modifierData = nbt.getCompoundTag("modifierData");
	}
	
	@Override
	public void setTargetedState(ImmutableClimateState state) {
		this.targetedState = state;
	}
	
	@Override
	public ImmutableClimateState getTargetedState() {
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

	public void setState(ClimateState state) {
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
		data.writeFloat(state.getTemperature());
		data.writeFloat(state.getHumidity());
		data.writeFloat(boundaryUp.getTemperature());
		data.writeFloat(boundaryUp.getHumidity());
		data.writeFloat(boundaryDown.getTemperature());
		data.writeFloat(boundaryDown.getHumidity());
		data.writeFloat(targetedState.getTemperature());
		data.writeFloat(targetedState.getHumidity());
		data.writeCompoundTag(modifierData);
	}

	@Override
	public void readData(PacketBufferForestry data) throws IOException {
		state.setTemperature(data.readFloat());
		state.setHumidity(data.readFloat());
		boundaryUp = new ImmutableClimateState(data.readFloat(), data.readFloat());
		boundaryDown = new ImmutableClimateState(data.readFloat(), data.readFloat());
		targetedState = new ImmutableClimateState(data.readFloat(), data.readFloat());
		modifierData = data.readCompoundTag();
	}
	
	@Override
	public ClimateState getState() {
		return state;
	}
	
	@Override
	public void addListaner(IClimateContainerListener listener) {
		listeners.addListaner(listener);
	}
	
	@Override
	public void removeListener(IClimateContainerListener listener) {
		listeners.removeListener(listener);
	}
	
	@Override
	public Collection<IClimateContainerListener> getListeners() {
		return listeners.getListeners();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof IClimateContainer)){
			return false;
		}
		IClimateContainer container = (IClimateContainer) obj;
		IClimateHousing parent = container.getParent();
		if(parent == null || this.parent == null){
			return false;
		}
		return this.parent.getCoordinates().equals(parent.getCoordinates());
	}
	
	@Override
	public int hashCode() {
		return parent.getCoordinates().hashCode();
	}
	
	@Nullable
	@Override
	@SideOnly(Side.CLIENT)
	public IClimateTable getTable(IClimateTableHelper helper, ClimateType type, boolean withTitle) {
		IClimateTable table;
		if(withTitle){
			table = new Table(Translator.translateToLocal("for.gui." + type.getName()));
		}else{
			table = new Table();
		}
		for(IClimateModifier modifier : GreenhouseManager.greenhouseHelper.getModifiers()){
			modifier.addTableEntries(this, state, modifierData, type, table);
		}
		return table;
	}
	
	private static final class ClimateContainerListeners implements IClimateContainerListener{
		private List<IClimateContainerListener> listeners = new LinkedList<>();

		@Override
		public boolean isClosed(IClimateContainer container) {
			for(IClimateContainerListener listener : listeners){
				if(!listener.isClosed(container)){
					return false;
				}
			}
			return true;
		}
		
		public void addListaner(IClimateContainerListener listener) {
			listeners.add(listener);
		}
		
		public void removeListener(IClimateContainerListener listener) {
			listeners.remove(listener);
		}
		
		public Collection<IClimateContainerListener> getListeners() {
			return listeners;
		}
		
	}

}
