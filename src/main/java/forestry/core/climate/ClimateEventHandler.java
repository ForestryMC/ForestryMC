package forestry.core.climate;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import forestry.api.core.climate.IClimateWorld;
import forestry.api.core.climate.IClimateWorld.ClimateChunk;
import forestry.api.core.climate.IClimatedPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClimateEventHandler {

	 @SubscribeEvent
	 public void chunkSave(ChunkDataEvent.Save event){
		 int dimensionID = event.getWorld().provider.getDimension();
		 World world = event.getWorld();
		 Chunk chunk = event.getChunk();
		 ChunkPos chunkPos = new ChunkPos(event.getChunk().xPosition, event.getChunk().zPosition);
		 IClimateWorld climateWorld = ClimateManager.getOrCreateWorld(world);
		 
		 if(climateWorld != null){
			ClimateChunk climateChunk = climateWorld.getClimateChunks().get(chunkPos);
			if(climateChunk != null){
				NBTTagCompound forestryTag = new NBTTagCompound();
				event.getData().setTag("Forestry", forestryTag);
				
				NBTTagList posList = new NBTTagList();
				for(IClimatedPosition position : climateChunk.getClimates().values()) {
					if(position != null){
						NBTTagCompound positionTag = position.writeToNBT(new NBTTagCompound());
						BlockPos pos = position.getPos();
						
						positionTag.setInteger("PosX", pos.getX());
						positionTag.setInteger("PosY", pos.getY());
						positionTag.setInteger("PosZ", pos.getZ());
						
						posList.appendTag(positionTag);
					}
				}
				
				forestryTag.setTag("Positions", forestryTag);
				if(!chunk.isLoaded()){
					climateWorld.removeChunk(chunkPos);
				}
			}
		 }
	 }
	 
	 @SubscribeEvent
	 public void chunkLoad(ChunkDataEvent.Load event){
		 int dimensionID = event.getWorld().provider.getDimension();
		 World world = event.getWorld();
		 Chunk chunk = event.getChunk();
		 
		 IClimateWorld climateWorld = ClimateManager.getOrCreateWorld(world);
		 
		 ChunkPos chunkPos = new ChunkPos(event.getChunk().xPosition, event.getChunk().zPosition);
		 NBTTagCompound chunkTag = event.getData();
		 
		 if(chunkTag.hasKey("Forestry")){
			 NBTTagCompound forestryTag = chunkTag.getCompoundTag("Forestry");
			 
			 Map<BlockPos, IClimatedPosition> positions = new HashMap();
			 NBTTagList posList = forestryTag.getTagList("Positions", 10);
			 for(int i = 0;i < posList.tagCount();i++){
				 NBTTagCompound positionTag = posList.getCompoundTagAt(i);
				 
					int posX = positionTag.getInteger("PosX");
					int posY = positionTag.getInteger("PosY");
					int posZ = positionTag.getInteger("PosZ");
					
					BlockPos pos = new BlockPos(posX, posY, posZ);
					IClimatedPosition position = new ClimatedPosition(climateWorld, pos);
					position.readFromNBT(positionTag);
					positions.put(pos, position);
			 }
			 
			 ClimateChunk climateChunk = climateWorld.getClimateChunks().get(chunkPos);
			 if(climateChunk == null){
				 climateChunk = new ClimateChunk(climateWorld, chunkPos);
			 }else{
				 for(Entry<BlockPos, IClimatedPosition> entry : positions.entrySet()){
					 climateChunk.getClimates().replace(entry.getKey(), entry.getValue());
				 }
			 }
			 
			 climateWorld.addChunk(climateChunk);
		 }else{
			 climateWorld.addChunk(new ClimateChunk(climateWorld, chunkPos));
		 }
	 }
	
}
