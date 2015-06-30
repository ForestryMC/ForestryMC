package forestry.apiculture.genetics;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.vect.IVect;

public class AlleleEffectFertile extends AlleleEffectThrottled {
	
	public static final int MAX_BLOCK_FIND_TRIES = 5;

	public AlleleEffectFertile() {
		super("fertile", false, 6, true, false);
	}

	@Override
	public IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		
		if (isHalted(storedData, housing)) {
			return storedData;
		}
		
		World world = housing.getWorld();
		ChunkCoordinates housingCoordinates = housing.getCoordinates();
		IVect area = getModifiedArea(genome, housing);
		
		int blockX = getRandomOffset(world.rand, housingCoordinates.posX, area.getX());
		int blockZ = getRandomOffset(world.rand, housingCoordinates.posZ, area.getZ());
		int blockMaxY = housingCoordinates.posY + (area.getY() / 2) + 1;
		int blockMinY = housingCoordinates.posY - (area.getY() / 2) - 1;
		
		for (int attempt = 0; attempt < MAX_BLOCK_FIND_TRIES; ++attempt) {
			if (tryTickColumn(world, blockX, blockZ, blockMaxY, blockMinY)) {
				break;
			}
			blockX = getRandomOffset(world.rand, housingCoordinates.posX, area.getX());
			blockZ = getRandomOffset(world.rand, housingCoordinates.posZ, area.getZ());
		}
		
		return storedData;
	}
	
	private static int getRandomOffset(Random random, int centrePos, int offset) {
		return centrePos + random.nextInt(offset) - (offset / 2);
	}

	private static boolean tryTickColumn(World world, int x, int z, int maxY, int minY) {
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
