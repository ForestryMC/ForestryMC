package forestry.core.climate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import forestry.api.core.climate.IClimatePosition;
import forestry.api.core.climate.IClimateRegion;
import forestry.api.multiblock.IGreenhouseController;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class ClimateRoom implements IClimateRegion, IStreamable {
	
	protected World world;
	protected IGreenhouseController controller;
	protected Map<BlockPos, IClimatePosition> positions;
	protected List<BlockPos> wallPositions;
	
	public ClimateRoom(ClimateRoom oldRoom, Map<BlockPos, IClimatePosition> innerPositions, List<BlockPos> wallPositions) {
		this.world = oldRoom.getWorld();
		this.controller = oldRoom.controller;
		Map<BlockPos, IClimatePosition> newPositions = new HashMap<>();
		for(Entry<BlockPos, IClimatePosition> positionEntry : innerPositions.entrySet()){
			IClimatePosition position = oldRoom.getPositions().get(positionEntry.getKey());
			if(position == null){
				newPositions.put(positionEntry.getKey(), positionEntry.getValue());
			}else{
				newPositions.put(positionEntry.getKey(), position);
			}
		}
		this.positions = newPositions;
		this.wallPositions = wallPositions;
	}
	
	public ClimateRoom(IGreenhouseController controller, Map<BlockPos, IClimatePosition> innerPositions, List<BlockPos> wallPositions) {
		this.world = controller.getWorldObj();
		this.controller = controller;
		this.positions = innerPositions;
		this.wallPositions = wallPositions;
	}
	
	public ClimateRoom(IGreenhouseController controller, NBTTagCompound nbtTag) {
		this.world = controller.getWorldObj();
		this.controller = controller;
		this.positions = new HashMap<>();
		readFromNBT(nbtTag);
	}
	
	@Override
	public void updateClimate() {
		for(Entry<BlockPos, IClimatePosition> position : positions.entrySet()){
			BlockPos pos = position.getKey();
			if(world.isBlockLoaded(pos)){
				updateSides(pos);
				if(!controller.isAssembled()){
					Biome biome = world.getBiome(pos);
					
					float biomeTemperature = biome.getTemperature();
					float biomeHumidity = biome.getRainfall();
					IClimatePosition climatedInfo = positions.get(pos);
					
					if(climatedInfo.getTemperature() != biomeTemperature){
						if(climatedInfo.getTemperature() > biomeTemperature){
							climatedInfo.addTemperature(-0.01F);
						}else{
							climatedInfo.addTemperature(0.01F);
						}
					}
					
					if(climatedInfo.getHumidity() != biomeHumidity){
						if(climatedInfo.getHumidity() > biomeHumidity){
							climatedInfo.addHumidity(-0.01F);
						}else{
							climatedInfo.addHumidity(0.01F);
						}
					}
				}
			}
		}
	}
	
	private void updateSides(BlockPos pos){
		IClimatePosition climatedInfo = positions.get(pos);
		for(EnumFacing facing : EnumFacing.VALUES){
			BlockPos facePos = pos.offset(facing);
			IClimatePosition climatedInfoFace = positions.get(facePos);
			if(climatedInfoFace != null){
				if(climatedInfoFace.getTemperature() >= 2.0F){
					if(climatedInfoFace.getTemperature() > 2.0F){
						climatedInfoFace.setTemperature(2.0F);
					}
					continue;
				}else if(climatedInfoFace.getTemperature() <= 0.0F){
					if(climatedInfoFace.getTemperature() < 0.0F){
						climatedInfoFace.setTemperature(0.0F);
					}
					continue;
				}
				if(climatedInfoFace.getHumidity() >= 2.0F){
					if(climatedInfoFace.getHumidity() > 2.0F){
						climatedInfoFace.setHumidity(2.0F);
					}
					continue;
				}else if(climatedInfoFace.getHumidity() <= 0.0F){
					if(climatedInfoFace.getHumidity() < 0.0F){
						climatedInfoFace.setHumidity(0.0F);
					}
					continue;
				}
				if(climatedInfo.getTemperature() > climatedInfoFace.getTemperature() + 0.01F){
					climatedInfo.addTemperature(-0.01F);
					climatedInfoFace.addTemperature(0.01F);
				}
				if(climatedInfo.getHumidity() > climatedInfoFace.getHumidity() + 0.01F){
					climatedInfo.addHumidity(-0.01F);
					climatedInfoFace.addHumidity(0.01F);
				}
			}
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		NBTTagList positionList = new NBTTagList();
		for(Entry<BlockPos, IClimatePosition> entry : positions.entrySet()){
			BlockPos pos = entry.getKey();
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("XPos", pos.getX());
			tag.setInteger("YPos", pos.getY());
			tag.setInteger("ZPos", pos.getZ());
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
			int xPos = positionTag.getInteger("XPos");
			int yPos = positionTag.getInteger("YPos");
			int zPos = positionTag.getInteger("ZPos");
			BlockPos pos = new BlockPos(xPos, yPos, zPos);
			ClimatePosition position = new ClimatePosition(this, pos);
			position.readFromNBT(positionTag);
			positions.put(pos, position);
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
	
	@Override
	public List<BlockPos> getOtherPositions() {
		return wallPositions;
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		if(!positions.isEmpty()){
			data.writeInt(positions.size());
			for(IClimatePosition pos : positions.values()){
				data.writeInt(pos.getPos().getX());
				data.writeInt(pos.getPos().getY());
				data.writeInt(pos.getPos().getZ());
				data.writeFloat(pos.getTemperature());
				data.writeFloat(pos.getHumidity());
			}
		}else{
			data.writeInt(0);
		}
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		int size = data.readInt();
		if(size != 0){
			positions = new HashMap<>();
			for(int index = 0;index < size;index++){
				int xPos = data.readInt();
				int yPos = data.readInt();
				int zPos = data.readInt();
				float temperature = data.readFloat();
				float humidity = data.readFloat();
				
				BlockPos pos = new BlockPos(xPos, yPos, zPos);
				positions.put(pos, new ClimatePosition(this, pos, temperature, humidity));
			}
		}
	}

}
