package forestry.apiculture.genetics.alleles;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.genetics.EffectData;
import forestry.core.utils.VectUtil;

public class AlleleEffectFungification extends AlleleEffectThrottled {

	private static final int MAX_BLOCK_FIND_TRIES = 10;
	private static final int ENTITY_THROTTLE = 6;

	public AlleleEffectFungification() {
		super("mycophilic", true, 20, false, false);
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
		World world = housing.getWorldObj();
		BlockPos housingCoordinates = housing.getCoordinates();
		Vec3i area = getModifiedArea(genome, housing);
		Vec3i halfArea = new Vec3i(area.getX() / 2, area.getY() / 2, area.getZ() / 2);

		for (int attempt = 0; attempt < MAX_BLOCK_FIND_TRIES; ++attempt) {
			BlockPos pos = VectUtil.getRandomPositionInArea(world.rand, area).subtract(halfArea).add(housingCoordinates);
			if (world.isBlockLoaded(pos)) {
				IBlockState blockState = world.getBlockState(pos);

				if (convertToMycelium(world, blockState, pos)) {
					return;
				} else if (growGiantMushroom(world, blockState, pos)) {
					return;
				}
			}
		}
	}

	private static void doEntityEffect(IBeeGenome genome, IBeeHousing housing) {
		List<EntityCow> cows = getEntitiesInRange(genome, housing, EntityCow.class);
		for (EntityCow cow : cows) {
			if (convertCowToMooshroom(cow)) {
				return;
			}
		}
	}

	private static boolean convertToMycelium(World world, IBlockState blockState, BlockPos pos) {
		Block block = blockState.getBlock();
		if (block == Blocks.GRASS || block == Blocks.DIRT && world.canBlockSeeSky(pos)) {
			world.setBlockState(pos, Blocks.MYCELIUM.getDefaultState());
			return true;
		}
		return false;
	}

	private static boolean growGiantMushroom(World world, IBlockState blockState, BlockPos pos) {
		Block block = blockState.getBlock();
		if (block instanceof BlockMushroom) {
			BlockMushroom mushroom = (BlockMushroom) block;
			mushroom.generateBigMushroom(world, pos, blockState, world.rand);
			return true;
		}
		return false;
	}

	private static boolean convertCowToMooshroom(EntityCow cow) {
		if (cow instanceof EntityMooshroom) {
			return false;
		}
		World world = cow.world;
		cow.setDead();
		EntityMooshroom mooshroom = new EntityMooshroom(world);
		mooshroom.setLocationAndAngles(cow.posX, cow.posY, cow.posZ, cow.rotationYaw, cow.rotationPitch);
		mooshroom.setHealth(cow.getHealth());
		mooshroom.renderYawOffset = cow.renderYawOffset;
		world.spawnEntity(mooshroom);
		world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, cow.posX, cow.posY + cow.height / 2.0F, cow.posZ, 0.0D, 0.0D, 0.0D);
		return true;
	}
}
