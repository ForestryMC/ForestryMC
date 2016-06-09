package forestry.core.climate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import forestry.api.core.climate.IClimateWorld;
import forestry.api.core.climate.IClimatedPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class ClimateWorld implements IClimateWorld{
	
	protected int dimensionID;
	protected World world;
	protected Map<ChunkPos, ClimateChunk> climateChunks = new ConcurrentHashMap();
	protected List<ClimateChunk> dirtyClimateChunks = new ArrayList<>();
	
	@Override
	public void addPosition(BlockPos pos){
		addPosition(createPosition(pos));
	}
	
	protected ClimateChunk getOrCreateChunk(ChunkPos chunkPos){
		ClimateChunk chunk = climateChunks.get(chunkPos);
		if(chunk == null){
			chunk = new ClimateChunk(this, chunkPos);
			climateChunks.put(chunkPos, chunk);
		}
		return chunk;
	}
	
	@Override
	public IClimatedPosition createPosition(BlockPos pos) {
		Biome biome = world.getBiome(pos);
		return new ClimatedPosition(this, pos, biome.getTemperature(), biome.getRainfall());
	}

	@Override
	public void addPositions(BlockPos minPos, BlockPos maxPos) {
		for(BlockPos pos : BlockPos.getAllInBox(minPos, maxPos)){
			addPosition(pos);
		}
	}

	@Override
	public void addPosition(IClimatedPosition position) {
		ChunkPos chunkPos = new ChunkPos(position.getPos());
		getOrCreateChunk(chunkPos).addPosition(position);
	}

	@Override
	public void addPositions(List<IClimatedPosition> positions) {
		for(IClimatedPosition position : positions){
			addPosition(position);
		}
	}
	
	@Override
	public IClimatedPosition getPosition(BlockPos pos) {
		ClimateChunk climateChunk = climateChunks.get(new ChunkPos(pos));
		if(climateChunk != null){
			return climateChunk.getClimates().get(pos);
		}
		return null;
	}
	
	@Override
	public void updateClimate() {
		for(ClimateChunk climateChunk : climateChunks.values()){
			for(IClimatedPosition position : climateChunk.getClimates().values()){
				position.updateClimate();
			}
		}
	}
	
	@Override
	public void addChunk(ClimateChunk climateChunk) {
		if(climateChunk == null){
			return;
		}
		climateChunks.put(climateChunk.getChunkPos(), climateChunk);
	}
	
	@Override
	public void removeChunk(ChunkPos chunkPos) {
		if(chunkPos == null){
			return;
		}
		climateChunks.remove(chunkPos);
	}

	@Override
	public Map<ChunkPos, ClimateChunk> getClimateChunks() {
		return climateChunks;
	}
	
	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public int getDimensionID() {
		return dimensionID;
	}
	
	@Override
	public void addDirtyClimateChunk(ClimateChunk chunk) {
		this.dirtyClimateChunks.add(chunk);
	}
	
	@Override
	public List<ClimateChunk> getDirtyClimateChunks() {
		return dirtyClimateChunks;
	}

}
