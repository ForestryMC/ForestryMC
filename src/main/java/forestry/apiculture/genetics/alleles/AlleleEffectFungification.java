package forestry.apiculture.genetics.alleles;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.genetics.EffectData;
import forestry.core.utils.vect.IVect;

public class AlleleEffectFungification extends AlleleEffectThrottled {

	private static final int MAX_BLOCK_FIND_TRIES = 10;
	private static final int ENTITY_THROTTLE = 6;
	
	public AlleleEffectFungification() {
		super("mycophilic", true, 10, false, false);
	}

	@Override
	public IEffectData validateStorage(IEffectData storedData) {
		if (storedData instanceof EffectData && ((EffectData) storedData).getIntSize() == 2) {
			return storedData;
		}

		return new EffectData(2, 0);
	}

	@Override
	public IEffectData doEffectThrottled(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {

		doBlockEffect(genome, housing);
		
		int entityThrottle = storedData.getInteger(1);
		if (entityThrottle >= ENTITY_THROTTLE) {
			doEntityEffect(genome, housing);
			entityThrottle = 0;
		} else {
			++entityThrottle;
		}
		storedData.setInteger(1, entityThrottle);
		
		return storedData;
	}

	private void doBlockEffect(IBeeGenome genome, IBeeHousing housing) {
		World world = housing.getWorld();
		ChunkCoordinates housingCoordinates = housing.getCoordinates();
		IVect area = getModifiedArea(genome, housing);
		
		int blockX = getRandomOffset(world.rand, housingCoordinates.posX, area.getX());
		int blockY = getRandomOffset(world.rand, housingCoordinates.posY, area.getY());
		int blockZ = getRandomOffset(world.rand, housingCoordinates.posZ, area.getZ());
		
		for (int attempt = 0; attempt < MAX_BLOCK_FIND_TRIES; ++attempt) {
			Block block = world.getBlock(blockX, blockY, blockZ);
			if (isSuitableForMycelium(world, block, blockX, blockY, blockZ)) {
				world.setBlock(blockX, blockY, blockZ, Blocks.mycelium);
				break;
			} else if (isSuitableForGrowth(block)) {
				doMushroomGrowth(block, world, blockX, blockY, blockZ);
				break;
			}
			blockX = getRandomOffset(world.rand, housingCoordinates.posX, area.getX());
			blockY = getRandomOffset(world.rand, housingCoordinates.posY, area.getY());
			blockZ = getRandomOffset(world.rand, housingCoordinates.posZ, area.getZ());
		}

	}

	private static void doEntityEffect(IBeeGenome genome, IBeeHousing housing) {
		List<EntityCow> cows = getEntitiesInRange(genome, housing, EntityCow.class);
		for (EntityCow cow : cows) {
			convertCowToMooshroom(cow);
		}
	}
	
	private static int getRandomOffset(Random random, int centrePos, int offset) {
		return centrePos + random.nextInt(offset) - (offset / 2);
	}
	
	private static boolean isSuitableForMycelium(World world, Block block, int blockX, int blockY, int blockZ) {
		return block == Blocks.grass || (block == Blocks.dirt && world.canBlockSeeTheSky(blockX, blockY, blockZ));
	}

	private static boolean isSuitableForGrowth(Block block) {
		return block == Blocks.red_mushroom || block == Blocks.brown_mushroom;
	}
	
	private static void convertCowToMooshroom(EntityCow cow) {
		if (cow instanceof EntityMooshroom) {
			return;
		}
		World worldObj = cow.worldObj;
		cow.setDead();
		EntityMooshroom mooshroom = new EntityMooshroom(worldObj);
		mooshroom.setLocationAndAngles(cow.posX, cow.posY, cow.posZ, cow.rotationYaw, cow.rotationPitch);
		mooshroom.setHealth(cow.getHealth());
		mooshroom.renderYawOffset = cow.renderYawOffset;
		worldObj.spawnEntityInWorld(mooshroom);
		worldObj.spawnParticle("largeexplode", cow.posX, cow.posY + (double) (cow.height / 2.0F), cow.posZ, 0.0D, 0.0D, 0.0D);
	}

	private static void doMushroomGrowth(Block block, World world, int blockX, int blockY, int blockZ) {
		WorldGenBigMushroom giantMushroomGenerator;

		if (block == Blocks.brown_mushroom) {
			giantMushroomGenerator = new WorldGenBigMushroom(0);
		} else {
			giantMushroomGenerator = new WorldGenBigMushroom(1);
		}

		world.setBlockToAir(blockX, blockY, blockZ);
		giantMushroomGenerator.generate(world, world.rand, blockX, blockY, blockZ);
	}
}
