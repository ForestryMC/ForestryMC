package forestry.apiculture.genetics;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class AlleleEffectFertile extends AlleleEffectThrottled {
	
	public static final int MAX_BLOCK_FIND_TRIES = 5;

	public AlleleEffectFertile(String uid) {
		super(uid, "fertile", false, 6, true, false);
	}

	@Override
	public IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		
		if (isHalted(storedData, housing)) {
			return storedData;
		}
		
		World world = housing.getWorld();
		int territorySize[] = getModifiedArea(genome, housing);
		
		int blockX = getRandomOffset(world.rand, housing.getXCoord(), territorySize[0]);
		int blockZ = getRandomOffset(world.rand, housing.getZCoord(), territorySize[2]);
		int blockMaxY = housing.getYCoord() + territorySize[1] / 2 + 1;
		int blockMinY = housing.getYCoord() - (territorySize[1] / 2) - 1;
		
		for (int attempt = 0; attempt < MAX_BLOCK_FIND_TRIES; ++attempt) {
			if (tryTickColumn(world, blockX, blockZ, blockMaxY, blockMinY)) {
				break;
			}
			blockX = getRandomOffset(world.rand, housing.getXCoord(), territorySize[0]);
			blockZ = getRandomOffset(world.rand, housing.getZCoord(), territorySize[2]);
		}
		
		return storedData;
	}
	
	private int getRandomOffset(Random random, int centrePos, int offset) {
		return centrePos + random.nextInt(offset) - (offset / 2);
	}

	private boolean tryTickColumn(World world, int x, int z, int maxY, int minY) {
		for (int y = maxY; y >= minY; --y) {
			if (!world.isAirBlock(x, y, z)) {
				Block block = world.getBlock(x, y, z);
				world.scheduleBlockUpdate(x, y, z, block, 5);
				return true;
			}
		}
		return false;
	}

}
