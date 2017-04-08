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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import forestry.api.climate.IClimateInfo;
import forestry.api.climate.IClimatePosition;
import forestry.api.climate.IClimateRegion;
import forestry.api.climate.IClimateSource;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClimateRegion implements IClimateRegion, IStreamable {
	public static final float CLIMATE_CHANGE = 0.01F;
	
	protected final World world;
	protected final IGreenhouseControllerInternal controller;
	protected final Set<IClimatePosition> positions;
	protected final Set<IClimateSource> sources;
	protected float temperature;
	protected float humidity;

	public ClimateRegion(IGreenhouseControllerInternal controller, Set<IClimatePosition> positions) {
		this.world = controller.getWorldObj();
		this.controller = controller;
		this.positions = positions;
		this.sources = new HashSet<>();
		calculateAverageClimate();
	}
	
	/**
	 * Creates an empty region.
	 */
	public ClimateRegion(IGreenhouseControllerInternal controller) {
		this.world = controller.getWorldObj();
		this.controller = controller;
		this.positions = new HashSet<>();
		this.sources = new HashSet<>();
	}


	public ClimateRegion(IGreenhouseControllerInternal controller, NBTTagCompound nbtTag) {
		this.world = controller.getWorldObj();
		this.controller = controller;
		this.positions = new HashSet<>();
		this.sources = new HashSet<>();
		readFromNBT(nbtTag);
		calculateAverageClimate();
	}

	@Override
	public void calculateAverageClimate(){
		humidity = 0.0F;
		temperature = 0.0F;
		if(!positions.isEmpty()){
			int positions = 0;
			for (IClimatePosition position : this.positions) {
				if (position != null) {
					positions++;
					humidity += position.getHumidity();
					temperature += position.getTemperature();
				}
			}
	
			temperature /= positions;
			humidity /= positions;
		}
	}

	@Override
	public void updateClimate(int ticks) {
		boolean hasChange = false;
		for (IClimateSource source : sources) {
			if (source != null) {
				if (ticks % source.getTicksForChange(this) == 0) {
					hasChange |= source.changeClimate(ticks, this);
				}
			}
		}
		if (ticks % getTicksPerUpdate() == 0) {
			for (IClimatePosition position : positions) {
				BlockPos pos = position.getPos();
				if (world.isBlockLoaded(pos)) {
					hasChange |= updateSides(position);
					if (!controller.isAssembled()) {
						hasChange |= returnClimateToDefault(position);
					}
				}
			}
		}
		if (hasChange) {
			calculateAverageClimate();
		}
	}

	protected boolean updateSides(IClimatePosition position) {
		BlockPos pos = position.getPos();
		IClimateInfo climateInfo = getControl(pos);
		boolean hasChange = false;
		if (climateInfo.getTemperature() != temperature || climateInfo.getHumidity() != humidity) {
			for (EnumFacing facing : EnumFacing.VALUES) {
				IClimatePosition infoFace = getPosition(pos.offset(facing));
				if (infoFace != null) {
					float infoTemp = infoFace.getTemperature();
					float infoHumid = infoFace.getHumidity();
					float posTemp = position.getTemperature();
					float posHumid = position.getHumidity();
					if (posTemp > infoTemp + CLIMATE_CHANGE) {
						float change = Math.min(CLIMATE_CHANGE, posTemp - infoTemp);
						position.addTemperature(-change);
						infoFace.addTemperature(change);
						hasChange = true;
					}
					if (posHumid > infoHumid + CLIMATE_CHANGE) {
						float change = Math.min(CLIMATE_CHANGE, posHumid - infoHumid);
						position.addHumidity(-change);
						infoFace.addHumidity(change);
						hasChange = true;
					}
				}
			}
		}
		return hasChange;
	}
	
	protected boolean returnClimateToDefault(IClimatePosition position){
		BlockPos pos = position.getPos();
		IClimateInfo climateInfo = getControl(pos);
		boolean hasChange = false;

		float infoTemp = climateInfo.getTemperature();
		float infoHumid = climateInfo.getHumidity();
		float posTemp = position.getTemperature();
		float posHumid = position.getHumidity();
		if (posTemp != infoTemp) {
			if (posTemp > infoTemp) {
				position.addTemperature(-Math.min(CLIMATE_CHANGE, posTemp - infoTemp));
				hasChange = true;
			} else {
				position.addTemperature(Math.min(CLIMATE_CHANGE, infoTemp - posTemp));
				hasChange = true;
			}
		}
		if (posHumid != infoHumid) {
			if (posHumid > infoHumid) {
				position.addHumidity(-Math.min(CLIMATE_CHANGE, posHumid - infoHumid));
				hasChange = true;
			} else {
				position.addHumidity(Math.min(CLIMATE_CHANGE, infoHumid - posHumid));
				hasChange = true;
			}
		}
		return hasChange;
	}

	protected IClimateInfo getControl(BlockPos pos) {
		if (world.isBlockLoaded(pos)) {
			if (!controller.isAssembled()) {
				return BiomeClimateInfo.getInfo(world.getBiome(pos));
			}
		}
		return controller.getControlClimate();
	}
	
	@Override
	public Collection<IClimatePosition> getPositions() {
		return positions;
	}
	
	@Override
	public IClimatePosition getPosition(BlockPos pos) {
		for(IClimatePosition position : positions){
			if(position.getPos().equals(pos)){
				return position;
			}
		}
		return null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		NBTTagList positionList = new NBTTagList();
		for (IClimatePosition positon : positions) {
			BlockPos pos = positon.getPos();
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("X", pos.getX());
			tag.setInteger("Y", pos.getY());
			tag.setInteger("Z", pos.getZ());
			positionList.appendTag(positon.writeToNBT(tag));
		}
		nbt.setTag("Positions", positionList);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagList positionList = nbt.getTagList("Positions", 10);
		for (int i = 0; i < positionList.tagCount(); i++) {
			NBTTagCompound positionTag = positionList.getCompoundTagAt(i);
			int xPos = positionTag.getInteger("X");
			int yPos = positionTag.getInteger("Y");
			int zPos = positionTag.getInteger("Z");
			BlockPos pos = new BlockPos(xPos, yPos, zPos);
			IClimatePosition position = getPosition(pos);
			if (position != null) {
				position.readFromNBT(positionTag);
			} else {
				positions.add(new ClimatePosition(this, pos, positionTag));
			}
		}
	}

	@Override
	public int getTicksPerUpdate() {
		return 20;
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public synchronized void setPosition(BlockPos pos, float temperature, float humidity) {
		IClimatePosition position = getPosition(pos);
		if (position != null) {
			position.setHumidity(humidity);
			position.setTemperature(temperature);
		} else {
			positions.add(new ClimatePosition(this, pos, temperature, humidity));
		}
	}

	@Override
	public synchronized void addSource(IClimateSource source) {
		if (!sources.contains(source)) {
			sources.add(source);
		}
	}

	@Override
	public synchronized void removeSource(IClimateSource source) {
		if (sources.contains(source)) {
			sources.remove(source);
		}
	}

	@Override
	public Collection<IClimateSource> getSources() {
		return sources;
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		if (!positions.isEmpty()) {
			data.writeInt(positions.size());
			data.writeFloat(temperature);
			data.writeFloat(humidity);
		} else {
			data.writeInt(0);
		}
	}

	@Override
	public void readData(PacketBufferForestry data) throws IOException {
		int size = data.readInt();
		if (size != 0) {
			temperature = data.readFloat();
			humidity = data.readFloat();
		}
	}

	@Override
	public float getAverageTemperature() {
		return temperature;
	}

	@Override
	public float getAverageHumidity() {
		return humidity;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ClimateRegion)){
			return false;
		}
		ClimateRegion region = (ClimateRegion) obj;
		if(region.controller != controller){
			return false;
		}
		return true;
	}

}
