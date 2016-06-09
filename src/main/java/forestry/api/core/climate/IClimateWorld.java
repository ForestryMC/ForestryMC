package forestry.api.core.climate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public interface IClimateWorld {
	
	@Nonnull
	World getWorld();
	
	int getDimensionID();
	
	void updateClimate();
	
	void addPositions(BlockPos minPos, BlockPos maxPos);
	
	void addPosition(BlockPos pos);
	
	void addPosition(IClimatedPosition position);
	
	void addPositions(List<IClimatedPosition> positions);
	
	IClimatedPosition getPosition(BlockPos pos);
	
	IClimatedPosition createPosition(BlockPos pos);
	
	void addChunk(ClimateChunk climateChunk);
	  
	void removeChunk(ChunkPos pos);
	
	@Nonnull 
	Map<ChunkPos, ClimateChunk> getClimateChunks();
	
	@Nonnull 
	List<ClimateChunk> getDirtyClimateChunks();
	
	void addDirtyClimateChunk(ClimateChunk chunk);
	
	public static class ClimateChunk{
		
		@Nonnull 
		protected final Map<BlockPos, IClimatedPosition> climates;
		@Nonnull 
		protected final IClimateWorld world;
		@Nonnull 
		protected final ChunkPos chunkPos;
		
		public ClimateChunk(@Nonnull IClimateWorld world, @Nonnull ChunkPos chunkPos) {
			this.world = world;
			this.chunkPos = chunkPos;
			climates = new HashMap<>();
			for(int x = chunkPos.getXStart();x < chunkPos.getXEnd();x++){
				for(int y = 0;y < world.getWorld().getActualHeight();y++){
					for(int z = chunkPos.getZStart();z < chunkPos.getZEnd();z++){
						BlockPos pos = new BlockPos(x, y, z);
						climates.put(pos, world.createPosition(pos));
					}
				}
			}
		}
		
		public void addPosition(IClimatedPosition position){
			world.addDirtyClimateChunk(this);
			climates.put(position.getPos(), position);
		}
		
		public Map<BlockPos, IClimatedPosition> getClimates() {
			return climates;
		}
		
		public IClimateWorld getWorld() {
			return world;
		}
		
		public ChunkPos getChunkPos() {
			return chunkPos;
		}
		
	}

}
