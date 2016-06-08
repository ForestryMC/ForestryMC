package forestry.api.core.climate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import forestry.api.core.ForestryAPI;
import forestry.api.core.climate.IClimateWorld.ClimateChunk;
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
	
	void setChunk(ClimateChunk climateChunk);
	
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
