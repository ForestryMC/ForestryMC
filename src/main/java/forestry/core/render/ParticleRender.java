package forestry.core.render;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;

import com.mojang.math.Vector3f;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.hives.IHiveTile;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.apiculture.entities.ParticleSnow;
import forestry.apiculture.genetics.alleles.AlleleEffect;
import forestry.apiculture.particles.ApicultureParticles;
import forestry.apiculture.particles.BeeParticleData;
import forestry.apiculture.particles.BeeTargetParticleData;
import forestry.core.config.Config;
import forestry.core.entities.ParticleIgnition;
import forestry.core.entities.ParticleSmoke;
import forestry.core.utils.VectUtil;
import forestry.core.utils.WorldUtils;

import genetics.api.individual.IGenome;

//import forestry.core.entities.ParticleClimate;
//import forestry.core.entities.ParticleHoneydust;

@OnlyIn(Dist.CLIENT)
public class ParticleRender {
	private static final DustParticleOptions HONEY_DUST = new DustParticleOptions(new Vector3f(0.9F, 0.75F, 0.0F), 1.0F);

	public static boolean shouldSpawnParticle(Level world) {
		if (!Config.enableParticleFX) {
			return false;
		}

		Minecraft mc = Minecraft.getInstance();
		ParticleStatus particleSetting = mc.options.particles().get();

		if (particleSetting == ParticleStatus.MINIMAL) { // minimal
			return world.random.nextInt(10) == 0;
		} else if (particleSetting == ParticleStatus.DECREASED) { // decreased
			return world.random.nextInt(3) != 0;
		} else { // all
			return true;
		}
	}

	public static void addBeeHiveFX(IBeeHousing housing, IGenome genome, List<BlockPos> flowerPositions) {
		ClientLevel world = WorldUtils.asClient(housing.getWorldObj());
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleEngine effectRenderer = Minecraft.getInstance().particleEngine;

		Vec3 particleStart = housing.getBeeFXCoordinates();

		// Avoid rendering bee particles that are too far away, they're very small.
		// At 32+ distance, have no bee particles. Make more particles up close.
		BlockPos playerPosition = Minecraft.getInstance().player.blockPosition();
		//TODO - correct?
		double playerDistanceSq = playerPosition.distSqr(new Vec3i(particleStart.x, particleStart.y, particleStart.z));
		if (world.random.nextInt(1024) < playerDistanceSq) {
			return;
		}

		int color = genome.getActiveAllele(BeeChromosomes.SPECIES).getSpriteColour(0);

		int randomInt = world.random.nextInt(100);

		if (housing instanceof IHiveTile) {
			if (((IHiveTile) housing).isAngry() || randomInt >= 85) {
				List<LivingEntity> entitiesInRange = AlleleEffect.getEntitiesInRange(genome, housing, LivingEntity.class);
				if (!entitiesInRange.isEmpty()) {
					LivingEntity entity = entitiesInRange.get(world.random.nextInt(entitiesInRange.size()));
					//Particle particle = new ParticleBeeTargetEntity(world, particleStart, entity, color);
					//effectRenderer.add(particle);
					world.addParticle(new BeeTargetParticleData(entity, color), particleStart.x, particleStart.y, particleStart.z, 0, 0, 0);
					return;
				}
			}
		}

		if (randomInt < 75 && !flowerPositions.isEmpty()) {
			BlockPos destination = flowerPositions.get(world.random.nextInt(flowerPositions.size()));
			//Particle particle = new ParticleBeeRoundTrip(world, particleStart, destination, color);
			//effectRenderer.add(particle);
			world.addParticle(new BeeParticleData(ApicultureParticles.BEE_ROUND_TRIP_PARTICLE, destination, color), particleStart.x, particleStart.y, particleStart.z, 0, 0, 0);
		} else {
			Vec3i area = AlleleEffect.getModifiedArea(genome, housing);
			Vec3i offset = housing.getCoordinates().offset(-area.getX() / 2, -area.getY() / 4, -area.getZ() / 2);
			BlockPos destination = VectUtil.getRandomPositionInArea(world.random, area).offset(offset);
			world.addParticle(new BeeParticleData(ApicultureParticles.BEE_EXPLORER_PARTICLE, destination, color), particleStart.x, particleStart.y, particleStart.z, 0, 0, 0);
			//Particle particle = new ParticleBeeExplore(world, particleStart, destination, color);
			//effectRenderer.add(particle);
		}
	}

	public static void addEntityHoneyDustFX(Level world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		world.addParticle(HONEY_DUST, x, y, z, 0, 0, 0);
		//		effectRenderer.addEffect(new ParticleHoneydust(world, x, y, z, 0, 0, 0));
	}

	public static void addClimateParticles(Level worldIn, BlockPos pos, RandomSource rand, EnumTemperature temperature, EnumHumidity humidity) {
		if (!shouldSpawnParticle(worldIn)) {
			return;
		}
		if (rand.nextFloat() >= 0.75F) {
			for (int i = 0; i < 3; i++) {
				Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(rand);
				int xOffset = facing.getStepX();
				int zOffset = facing.getStepZ();
				double x = pos.getX() + 0.5 + (xOffset * 8 + ((1 - Mth.abs(xOffset)) * (0.5 - rand.nextFloat()) * 8)) / 16.0;
				double y = pos.getY() + (0.75 + rand.nextFloat() * 14.5) / 16.0;
				double z = pos.getZ() + 0.5 + (zOffset * 8 + ((1 - Mth.abs(zOffset)) * (0.5 - rand.nextFloat()) * 8)) / 16.0;
				if (rand.nextBoolean()) {
					ParticleRender.addEntityClimateParticle(worldIn, x, y, z, temperature.color);
				} else {
					ParticleRender.addEntityClimateParticle(worldIn, x, y, z, humidity.color);
				}
			}
		}
	}

	public static void addEntityClimateParticle(Level world, double x, double y, double z, int color) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleEngine effectRenderer = Minecraft.getInstance().particleEngine;
		//		effectRenderer.addEffect(new ParticleClimate(world, x, y, z, color));
		//TODO particles
	}

	public static void addTransformParticles(Level worldIn, BlockPos pos, RandomSource rand) {
		if (!shouldSpawnParticle(worldIn)) {
			return;
		}
		if (rand.nextFloat() >= 0.65F) {
			for (int i = 0; i < 3; i++) {
				Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(rand);
				int xOffset = facing.getStepX();
				int zOffset = facing.getStepZ();
				double x = pos.getX() + 0.5 + (xOffset * 8 + ((1 - Mth.abs(xOffset)) * (0.5 - rand.nextFloat()) * 8)) / 16.0;
				double y = pos.getY() + (0.75 + rand.nextFloat() * 14.5) / 16.0;
				double z = pos.getZ() + 0.5 + (zOffset * 8 + ((1 - Mth.abs(zOffset)) * (0.5 - rand.nextFloat()) * 8)) / 16.0;
				ParticleRender.addEntityTransformParticle(worldIn, x, y, z);
			}
		}
	}

	public static void addEntityTransformParticle(Level world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleEngine effectRenderer = Minecraft.getInstance().particleEngine;
		//		effectRenderer.addEffect(new ParticleClimate(world, x, y, z));
		//TODO particles
	}

	public static void addEntityExplodeFX(Level world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleEngine effectRenderer = Minecraft.getInstance().particleEngine;
		//TODO particle data
		Particle Particle = effectRenderer.createParticle(DustParticleOptions.REDSTONE, x, y, z, 0, 0, 0);
		effectRenderer.add(Particle);
	}

	public static void addEntitySnowFX(Level world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleEngine effectRenderer = Minecraft.getInstance().particleEngine;
		effectRenderer.add(new ParticleSnow(WorldUtils.asClient(world), x + world.random.nextGaussian(), y, z + world.random.nextGaussian()));
	}

	public static void addEntityIgnitionFX(ClientLevel world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleEngine effectRenderer = Minecraft.getInstance().particleEngine;
		effectRenderer.add(new ParticleIgnition(world, x, y, z));
	}

	public static void addEntitySmokeFX(Level world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleEngine effectRenderer = Minecraft.getInstance().particleEngine;
		effectRenderer.add(new ParticleSmoke(WorldUtils.asClient(world), x, y, z));
	}

	public static void addEntityPotionFX(Level world, double x, double y, double z, int color) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;

		ParticleEngine effectRenderer = Minecraft.getInstance().particleEngine;
		//TODO - maybe EFFECT?
		//TODO particle data
		Particle particle = effectRenderer.createParticle(DustParticleOptions.REDSTONE, x, y, z, 0, 0, 0);
		if (particle != null) {
			particle.setColor(red, green, blue);
			effectRenderer.add(particle);
		}
	}

	public static void addPortalFx(Level world, BlockPos pos, RandomSource rand) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		int j = rand.nextInt(2) * 2 - 1;
		int k = rand.nextInt(2) * 2 - 1;
		double xPos = (double) pos.getX() + 0.5D + 0.25D * (double) j;
		double yPos = (float) pos.getY() + rand.nextFloat();
		double zPos = (double) pos.getZ() + 0.5D + 0.25D * (double) k;
		double xSpeed = rand.nextFloat() * (float) j;
		double ySpeed = ((double) rand.nextFloat() - 0.5D) * 0.125D;
		double zSpeed = rand.nextFloat() * (float) k;
		ParticleEngine effectRenderer = Minecraft.getInstance().particleEngine;
		//TODO particle data
		Particle particle = effectRenderer.createParticle(DustParticleOptions.REDSTONE, xPos, yPos, zPos, xSpeed, ySpeed, zSpeed);
		if (particle != null) {
			effectRenderer.add(particle);
		}
	}

	public static void addEntityBiodustFX(Level world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleEngine effectRenderer = Minecraft.getInstance().particleEngine;
		//TODO particle data
		Particle particle = effectRenderer.createParticle(DustParticleOptions.REDSTONE, x, y, z, 0, 0, 0);
		if (particle != null) {
			effectRenderer.add(particle);
		}
	}
}
