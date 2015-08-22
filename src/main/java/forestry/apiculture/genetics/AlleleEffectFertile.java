package forestry.apiculture.genetics;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;

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
		
		int blockX = getRandomOffset(world.rand, housing.getCoords().getX(), territorySize[0]);
		int blockZ = getRandomOffset(world.rand, housing.getCoords().getZ(), territorySize[2]);
		int blockMaxY = housing.getCoords().getY() + territorySize[1] / 2 + 1;
		int blockMinY = housing.getCoords().getY() - (territorySize[1] / 2) - 1;
		
		for (int attempt = 0; attempt < MAX_BLOCK_FIND_TRIES; ++attempt) {
			if (tryTickColumn(world, blockX, blockZ, blockMaxY, blockMinY)) {
				break;
			}
			blockX = getRandomOffset(world.rand, housing.getCoords().getX(), territorySize[0]);
			blockZ = getRandomOffset(world.rand, housing.getCoords().getZ(), territorySize[2]);
		}
		
		return storedData;
	}
	
	private int getRandomOffset(Random random, int centrePos, int offset) {
		return centrePos + random.nextInt(offset) - (offset / 2);
	}

	private boolean tryTickColumn(World world, int x, int z, int maxY, int minY) {
		for (int y = maxY; y >= minY; --y) {
			if (!world.isAirBlock(new BlockPos(x, y, z))) {
				Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
				world.scheduleBlockUpdate(new BlockPos(x, y, z), block, 0, 5);
				return true;
			}
		}
		return false;
	}

}
