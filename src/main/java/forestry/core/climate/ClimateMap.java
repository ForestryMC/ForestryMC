package forestry.core.climate;

import java.util.ArrayList;
import java.util.List;

import forestry.api.core.climate.IClimateMap;
import forestry.api.core.climate.IClimatedPosition;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class ClimateMap implements IClimateMap {

	protected final List<IClimatedPosition> positions;
	protected final World world;
	protected final BlockPos minimumPos;
	protected final BlockPos maximumPos;
	
	public ClimateMap(World world, BlockPos minimumPos, BlockPos maximumPos, List<IClimatedPosition> positions) {
		this.world = world;
		this.minimumPos = minimumPos;
		this.maximumPos = maximumPos;
		this.positions = positions;
	}
	
	public ClimateMap(World world, BlockPos minimumPos, BlockPos maximumPos) {
		this.world = world;
		this.minimumPos = minimumPos;
		this.maximumPos = maximumPos;
		positions = new ArrayList();
		for(BlockPos pos : BlockPos.getAllInBox(minimumPos, maximumPos)){
			Biome biome = world.getBiome(pos);
			positions.add(new ClimatedPosition(this, pos, biome.getTemperature(), biome.getRainfall()));
		}
	}
	
	@Override
	public List<IClimatedPosition> getPositions() {
		return positions;
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public BlockPos getMinimumPos() {
		return minimumPos;
	}

	@Override
	public BlockPos getMaximumPos() {
		return maximumPos;
	}
	
	protected IClimatedPosition getPosition(BlockPos pos){
		for(IClimatedPosition climatedPosition : positions){
			if(climatedPosition.getPos().equals(pos)){
				return climatedPosition;
			}
		}
		return null;
	}

	@Override
	public void updateClimate() {
		for(IClimatedPosition position : positions) {
			for(EnumFacing facing : EnumFacing.VALUES){
				BlockPos facePos = position.getPos().offset(facing);
				IClimatedPosition climatedPosition = getPosition(facePos);
				if(climatedPosition != null){
					if(position.getTemperature() > climatedPosition.getTemperature() + 0.01F){
						position.setTemperature(position.getTemperature() - 0.01F);
						climatedPosition.setTemperature(climatedPosition.getTemperature() + 0.01F);
					}
					if(position.getHumidity() > climatedPosition.getHumidity() + 0.01F){
						position.setHumidity(position.getHumidity() - 0.01F);
						climatedPosition.setHumidity(climatedPosition.getHumidity() + 0.01F);
					}
				}
			}
		}
	}

}
