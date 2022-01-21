package forestry.apiculture.genetics.alleles;

import java.util.List;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.genetics.EffectData;
import forestry.core.utils.VectUtil;

import genetics.api.individual.IGenome;

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
	public IEffectData doEffectThrottled(IGenome genome, IEffectData storedData, IBeeHousing housing) {

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

	private void doBlockEffect(IGenome genome, IBeeHousing housing) {
		Level world = housing.getWorldObj();
		BlockPos housingCoordinates = housing.getCoordinates();
		Vec3i area = getModifiedArea(genome, housing);
		Vec3i halfArea = new Vec3i(area.getX() / 2, area.getY() / 2, area.getZ() / 2);

		for (int attempt = 0; attempt < MAX_BLOCK_FIND_TRIES; ++attempt) {
			BlockPos pos = VectUtil.getRandomPositionInArea(world.random, area).subtract(halfArea).offset(housingCoordinates);
			if (world.hasChunkAt(pos)) {
				BlockState blockState = world.getBlockState(pos);

				if (convertToMycelium(world, blockState, pos)) {
					return;
				} else if (growGiantMushroom((ServerLevel) world, blockState, pos)) {
					return;
				}
			}
		}
	}

	private static void doEntityEffect(IGenome genome, IBeeHousing housing) {
		List<Cow> cows = getEntitiesInRange(genome, housing, Cow.class);
		for (Cow cow : cows) {
			if (convertCowToMooshroom(cow)) {
				return;
			}
		}
	}

	private static boolean convertToMycelium(Level world, BlockState blockState, BlockPos pos) {
		Block block = blockState.getBlock();
		if (block == Blocks.GRASS || block == Blocks.DIRT && world.canSeeSkyFromBelowWater(pos)) {
			world.setBlockAndUpdate(pos, Blocks.MYCELIUM.defaultBlockState());
			return true;
		}
		return false;
	}

	private static boolean growGiantMushroom(ServerLevel world, BlockState blockState, BlockPos pos) {
		Block block = blockState.getBlock();
		if (block instanceof MushroomBlock) {
			MushroomBlock mushroom = (MushroomBlock) block;
			mushroom.growMushroom(world, pos, blockState, world.random);
			return true;
		}
		return false;
	}

	private static boolean convertCowToMooshroom(Cow cow) {
		if (cow instanceof MushroomCow) {
			return false;
		}
		Level world = cow.level;
		cow.remove();
		MushroomCow mooshroom = new MushroomCow(EntityType.MOOSHROOM, world);
		mooshroom.moveTo(cow.getX(), cow.getY(), cow.getZ(), cow.yRot, cow.xRot);
		mooshroom.setHealth(cow.getHealth());
		mooshroom.yBodyRot = cow.yBodyRot;
		world.addFreshEntity(mooshroom);
		world.addParticle(ParticleTypes.EXPLOSION, cow.getX(), cow.getY() + cow.getBbHeight() / 2.0F, cow.getZ(), 0.0D, 0.0D, 0.0D);
		return true;
	}
}
