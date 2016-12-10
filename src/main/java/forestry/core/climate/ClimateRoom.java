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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import forestry.api.climate.IClimateControl;
import forestry.api.climate.IClimatePosition;
import forestry.api.climate.IClimateRegion;
import forestry.api.climate.IClimateSource;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import forestry.core.utils.ClimateUtil;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClimateRoom implements IClimateRegion, IStreamable {
	
	protected final World world;
	protected final IGreenhouseControllerInternal controller;
	protected final Map<BlockPos, IClimatePosition> positions;
	protected final List<BlockPos> wallPositions;
	protected final List<IClimateSource> sources;
	protected float temperature;
	protected float humidity;
	
	public ClimateRoom(ClimateRoom oldRoom, Map<BlockPos, IClimatePosition> newPositions, List<BlockPos> newWallPositions) {
		this.world = oldRoom.getWorld();
		this.controller = oldRoom.controller;
		this.positions = new HashMap<>();
		this.wallPositions = newWallPositions;
		this.sources = new ArrayList<>();
		for(Entry<BlockPos, IClimatePosition> positionEntry : newPositions.entrySet()){
			BlockPos pos = positionEntry.getKey();
			IClimatePosition position = positionEntry.getValue();
			IClimatePosition oldPosition = oldRoom.getPositions().get(pos);
			float temperature = position.getTemperature();
			float humidity = position.getHumidity();
			if(oldPosition != null){
				temperature = oldPosition.getTemperature();
				humidity = oldPosition.getHumidity();
			}
			addPosition(pos, temperature, humidity);
		}
		this.temperature = getExactTemperature();
		this.humidity = getExactHumidity();
	}
	
	public ClimateRoom(IGreenhouseControllerInternal controller, Map<BlockPos, IClimatePosition> innerPositions, List<BlockPos> wallPositions) {
		this.world = controller.getWorldObj();
		this.controller = controller;
		this.positions = innerPositions;
		this.wallPositions = wallPositions;
		this.sources = new ArrayList<>();
		this.temperature = getExactTemperature();
		this.humidity = getExactHumidity();
	}
	
	public ClimateRoom(IGreenhouseControllerInternal controller, List<BlockPos> wallPositions, NBTTagCompound nbtTag) {
		this.world = controller.getWorldObj();
		this.controller = controller;
		this.positions = new HashMap<>();
		this.wallPositions = new ArrayList<>();
		this.sources = new ArrayList<>();
		readFromNBT(nbtTag);
		this.temperature = getExactTemperature();
		this.humidity = getExactHumidity();
	}
	
	private float getExactTemperature() {
		float temperature = 0.0F;
		int positions = 0;
		
		for(IClimatePosition position : this.positions.values()){
			if(position != null){
				positions++;
				temperature+=position.getTemperature();
			}
		}
		return temperature / positions;
	}

	private float getExactHumidity() {
		float humidity = 0.0F;
		int positions = 0;
		
		for(IClimatePosition position : this.positions.values()){
			if(position != null){
				positions++;
				humidity+=position.getHumidity();
			}
		}
		return humidity / positions;
	}
	
	@Override
	public void updateClimate(int ticks) {
		boolean hasChange = false;;
		for(IClimateSource source : sources){
			if(source != null){
				if(ticks % source.getTicksForChange(this) == 0){
					hasChange |= source.changeClimate(ticks, this);
				}
			}
		}
		if(ticks % getTicksPerUpdate() == 0){
			for(Entry<BlockPos, IClimatePosition> position : positions.entrySet()){
				BlockPos pos = position.getKey();
				if(world.isBlockLoaded(pos)){
					hasChange |= updateSides(pos);
					
					if(!controller.isAssembled()){
						IClimateControl climateControl = getControl(pos);
						IClimatePosition climatedInfo = positions.get(pos);
						
						if(climatedInfo.getTemperature() != climateControl.getControlTemperature()){
							if(climatedInfo.getTemperature() > climateControl.getControlTemperature()){
								climatedInfo.addTemperature(-Math.min(0.01F, climatedInfo.getTemperature() - climateControl.getControlTemperature()));
								hasChange = true;
							}else{
								climatedInfo.addTemperature(Math.min(0.01F, climateControl.getControlTemperature() - climatedInfo.getTemperature()));
								hasChange = true;
							}
						}
						if(climatedInfo.getHumidity() != climateControl.getControlHumidity()){
							if(climatedInfo.getHumidity() > climateControl.getControlHumidity()){
								climatedInfo.addHumidity(-Math.min(0.01F, climatedInfo.getHumidity() - climateControl.getControlHumidity()));
								hasChange = true;
							}else{
								climatedInfo.addHumidity(Math.min(0.01F, climateControl.getControlHumidity() - climatedInfo.getHumidity()));
								hasChange = true;
							}
						}
					}
				}
			}
		}
		if(hasChange){
			temperature = getExactTemperature();
			humidity = getExactHumidity();
		}
	}
	
	protected boolean updateSides(BlockPos pos){
		IClimatePosition climatedInfo = positions.get(pos);
		IClimateControl climateControl = getControl(pos);
		boolean hasChange = false;
		if(climateControl.getControlTemperature() != temperature || climateControl.getControlHumidity() != humidity) {
			for(EnumFacing facing : EnumFacing.VALUES){
				IClimatePosition climatedInfoFace = positions.get(pos.offset(facing));
				if(climatedInfoFace != null){
					if(climatedInfo.getTemperature() > climatedInfoFace.getTemperature() + 0.01F){
						float change = Math.min(0.01F, climatedInfo.getTemperature() - climatedInfoFace.getTemperature());
						climatedInfo.addTemperature(-change);
						climatedInfoFace.addTemperature(change);
						hasChange = true;
					}
					if(climatedInfo.getHumidity() > climatedInfoFace.getHumidity() + 0.01F){
						float change = Math.min(0.01F, climatedInfo.getHumidity() - climatedInfoFace.getHumidity());
						climatedInfo.addHumidity(-change);
						climatedInfoFace.addHumidity(change);
						hasChange = true;
					}
				}
			}
		}
		return hasChange;
	}
	
	protected IClimateControl getControl(BlockPos pos){
		if(world.isBlockLoaded(pos)){
			if(!controller.isAssembled()){
				return BiomeClimateControl.getControl(world.getBiome(pos));
			}
		}
		return controller.getClimateControl();
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		NBTTagList positionList = new NBTTagList();
		for(Entry<BlockPos, IClimatePosition> entry : positions.entrySet()){
			BlockPos pos = entry.getKey();
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("X", pos.getX());
			tag.setInteger("Y", pos.getY());
			tag.setInteger("Z", pos.getZ());
			positionList.appendTag(entry.getValue().writeToNBT(tag));
		}
		nbt.setTag("Positions", positionList);
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagList positionList = nbt.getTagList("Positions", 10);
		for(int i = 0;i < positionList.tagCount();i++){
			NBTTagCompound positionTag = positionList.getCompoundTagAt(i);
			int xPos = positionTag.getInteger("X");
			int yPos = positionTag.getInteger("Y");
			int zPos = positionTag.getInteger("Z");
			BlockPos pos = new BlockPos(xPos, yPos, zPos);
			IClimatePosition position = positions.get(pos);
			if(position != null){
				position.readFromNBT(positionTag);
			}else{
				positions.put(pos, new ClimatePosition(this, pos, positionTag));
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
	public Map<BlockPos, IClimatePosition> getPositions() {
		return positions;
	}
	
	public synchronized void addPosition(BlockPos pos, float temperature, float humidity){
		IClimatePosition otherPosition = positions.get(pos);
		if(otherPosition != null){
			otherPosition.setHumidity(humidity);
			otherPosition.setTemperature(temperature);
		}else{
			positions.put(pos, new ClimatePosition(this, pos, temperature, humidity));
		}
	}
	
	@Override
	public synchronized void addSource(IClimateSource source) {
		if(!sources.contains(source)){
			sources.add(source);
		}
	}
	
	@Override
	public synchronized void removeSource(IClimateSource source) {
		if(sources.contains(source)){
			sources.remove(source);
		}
	}
	
	@Override
	public Collection<IClimateSource> getSources() {
		return sources;
	}
	
	@Override
	public List<BlockPos> getOtherPositions() {
		return wallPositions;
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		if(!positions.isEmpty()){
			data.writeInt(positions.size());
			for(IClimatePosition pos : positions.values()){
				ClimateUtil.writeRoomPositionData(pos, data);
			}
			data.writeFloat(temperature);
			data.writeFloat(humidity);
		}else{
			data.writeInt(0);
		}
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		int size = data.readInt();
		if(size != 0){
			positions.clear();;
			for(int index = 0;index < size;index++){
				ClimateUtil.readRoomPositionData(this, data);
			}
			temperature = data.readFloat();
			humidity = data.readFloat();
		}
	}
	
	@Override
	public float getTemperature() {
		return temperature;
	}
	
	@Override
	public float getHumidity() {
		return humidity;
	}

}
