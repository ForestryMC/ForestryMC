package forestry.apiculture.genetics;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.genetics.EffectData;

public class AlleleEffectFungification extends AlleleEffectThrottled {

	public static final int MAX_BLOCK_FIND_TRIES = 10;
	public static final int ENTITY_THROTTLE = 6;
	
	public AlleleEffectFungification(String uid) {
		super(uid, "mycophilic", true, 10, false, false);
	}

	@Override
	public IEffectData validateStorage(IEffectData storedData) {
		if (storedData instanceof EffectData && ((EffectData) storedData).getIntSize() == 2) {
			return storedData;
		}

		return new EffectData(2, 0);
	}

	@Override
	public IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		if (isHalted(storedData, housing)) {
			return storedData;
		}
		
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
		int territorySize[] = getModifiedArea(genome, housing);
		
		int blockX = getRandomOffset(world.rand, housing.getCoords().getX(), territorySize[0]);
		int blockY = getRandomOffset(world.rand, housing.getCoords().getY(), territorySize[1]);
		int blockZ = getRandomOffset(world.rand, housing.getCoords().getZ(), territorySize[2]);
		
		for (int attempt = 0; attempt < MAX_BLOCK_FIND_TRIES; ++attempt) {
			Block block = world.getBlockState(new BlockPos(blockX, blockY, blockZ)).getBlock();
			if (isSuitableForMycelium(world, block, blockX, blockY, blockZ)) {
				world.setBlockState(new BlockPos(blockX, blockY, blockZ), Blocks.mycelium.getDefaultState());
				break;
			} else if (isSuitableForGrowth(block)) {
				doMushroomGrowth(block, world, blockX, blockY, blockZ);
				break;
			}
			blockX = getRandomOffset(world.rand, housing.getCoords().getX(), territorySize[0]);
			blockY = getRandomOffset(world.rand, housing.getCoords().getY(), territorySize[1]);
			blockZ = getRandomOffset(world.rand, housing.getCoords().getZ(), territorySize[2]);
		}

	}

	private void doEntityEffect(IBeeGenome genome, IBeeHousing housing) {
		AxisAlignedBB aabb = this.getBounding(genome, housing, 1f);
		World world = housing.getWorld();

		List entities = world.getEntitiesWithinAABB(EntityCow.class, aabb);
		for (Object entity : entities) {
			if (entity instanceof EntityCow && !(entity instanceof EntityMooshroom)) {
				convertCowToMooshroom((EntityCow) entity);
				break;
			}
		}
	}
	
	private int getRandomOffset(Random random, int centrePos, int offset) {
		return centrePos + random.nextInt(offset) - (offset / 2);
	}
	
	private boolean isSuitableForMycelium(World world, Block block, int blockX, int blockY, int blockZ) {
		return block == Blocks.grass || (block == Blocks.dirt && world.canBlockSeeSky(new BlockPos(blockX, blockY, blockZ)));
	}

	private boolean isSuitableForGrowth(Block block) {
		return block == Blocks.red_mushroom || block == Blocks.brown_mushroom;
	}
	
	private static void convertCowToMooshroom(EntityCow cow) {
		World worldObj = cow.worldObj;
		cow.setDead();
		EntityMooshroom mooshroom = new EntityMooshroom(worldObj);
		mooshroom.setLocationAndAngles(cow.posX, cow.posY, cow.posZ, cow.rotationYaw, cow.rotationPitch);
		mooshroom.setHealth(cow.getHealth());
		mooshroom.renderYawOffset = cow.renderYawOffset;
		worldObj.spawnEntityInWorld(mooshroom);
		worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, cow.posX, cow.posY + (double) (cow.height / 2.0F), cow.posZ, 0.0D, 0.0D, 0.0D);
	}

	private void doMushroomGrowth(Block block, World world, int blockX, int blockY, int blockZ) {
		WorldGenBigMushroom giantMushroomGenerator;

		if (block == Blocks.brown_mushroom) {
			giantMushroomGenerator = new WorldGenBigMushroom(0);
		} else {
			giantMushroomGenerator = new WorldGenBigMushroom(1);
		}

		world.setBlockToAir(new BlockPos(blockX, blockY, blockZ));
		giantMushroomGenerator.generate(world, world.rand, new BlockPos(blockX, blockY, blockZ));
	}
}
