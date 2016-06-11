package forestry.core.climate;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import forestry.api.core.climate.IClimatePosition;
import forestry.api.core.climate.IClimateRegion;
import forestry.api.multiblock.IGreenhouseController;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class ClimateRoom implements IClimateRegion {
	
	protected World world;
	protected IGreenhouseController controller;
	protected Map<BlockPos, IClimatePosition> positions;
	
	public ClimateRoom(ClimateRoom room, Map<BlockPos, IClimatePosition> innerPositions) {
		this.world = room.getWorld();
		this.controller = room.controller;
		Map<BlockPos, IClimatePosition> newPositions = new HashMap<>();
		for(Entry<BlockPos, IClimatePosition> positionEntry : innerPositions.entrySet()){
			IClimatePosition position = this.positions.get(positionEntry.getKey());
			if(position == null){
				newPositions.put(positionEntry.getKey(), positionEntry.getValue());
			}else{
				newPositions.put(positionEntry.getKey(), position);
			}
		}
		this.positions = newPositions;
	}
	
	public ClimateRoom(IGreenhouseController controller, Map<BlockPos, IClimatePosition> innerPositions) {
		this.world = controller.getWorldObj();
		this.controller = controller;
		this.positions = innerPositions;
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
			ClimatedPosition position = new ClimatedPosition(this, pos);
			position.readFromNBT(positionTag);
			positions.put(pos, position);
		}
	}
	
	@Override
	public World getWorld() {
		return world;
	}
	
	@Override
	public Map<BlockPos, IClimatePosition> getPositions() {
		return positions;
	}

}
