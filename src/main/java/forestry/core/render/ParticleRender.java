package forestry.core.render;

import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IHiveTile;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.apiculture.entities.ParticleBeeExplore;
import forestry.apiculture.entities.ParticleBeeRoundTrip;
import forestry.apiculture.entities.ParticleBeeTargetEntity;
import forestry.apiculture.genetics.alleles.AlleleEffect;
import forestry.core.config.Config;
import forestry.core.entities.ParticleClimate;
import forestry.core.entities.ParticleHoneydust;
import forestry.core.entities.ParticleIgnition;
import forestry.core.entities.ParticleSmoke;
import forestry.core.entities.ParticleSnow;
import forestry.core.utils.VectUtil;

@SideOnly(Side.CLIENT)
public class ParticleRender {
	public static boolean shouldSpawnParticle(World world) {
		if (!Config.enableParticleFX) {
			return false;
		}

		Minecraft mc = Minecraft.getMinecraft();
		int particleSetting = mc.gameSettings.particleSetting;

		if (particleSetting == 2) { // minimal
			return world.rand.nextInt(10) == 0;
		} else if (particleSetting == 1) { // decreased
			return world.rand.nextInt(3) != 0;
		} else { // all
			return true;
		}
	}

	public static void addBeeHiveFX(IBeeHousing housing, IBeeGenome genome, List<BlockPos> flowerPositions) {
		World world = housing.getWorldObj();
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;

		Vec3d particleStart = housing.getBeeFXCoordinates();

		// Avoid rendering bee particles that are too far away, they're very small.
		// At 32+ distance, have no bee particles. Make more particles up close.
		BlockPos playerPosition = Minecraft.getMinecraft().player.getPosition();
		double playerDistanceSq = playerPosition.distanceSqToCenter(particleStart.x, particleStart.y, particleStart.z);
		if (world.rand.nextInt(1024) < playerDistanceSq) {
			return;
		}

		int color = genome.getPrimary().getSpriteColour(0);

		int randomInt = world.rand.nextInt(100);

		if (housing instanceof IHiveTile) {
			if (((IHiveTile) housing).isAngry() || randomInt >= 85) {
				List<EntityLivingBase> entitiesInRange = AlleleEffect.getEntitiesInRange(genome, housing, EntityLivingBase.class);
				if (!entitiesInRange.isEmpty()) {
					EntityLivingBase entity = entitiesInRange.get(world.rand.nextInt(entitiesInRange.size()));
					Particle particle = new ParticleBeeTargetEntity(world, particleStart, entity, color);
					effectRenderer.addEffect(particle);
					return;
				}
			}
		}

		if (randomInt < 75 && !flowerPositions.isEmpty()) {
			BlockPos destination = flowerPositions.get(world.rand.nextInt(flowerPositions.size()));
			Particle particle = new ParticleBeeRoundTrip(world, particleStart, destination, color);
			effectRenderer.addEffect(particle);
		} else {
			Vec3i area = AlleleEffect.getModifiedArea(genome, housing);
			Vec3i offset = housing.getCoordinates().add(-area.getX() / 2, -area.getY() / 4, -area.getZ() / 2);
			BlockPos destination = VectUtil.getRandomPositionInArea(world.rand, area).add(offset);
			Particle particle = new ParticleBeeExplore(world, particleStart, destination, color);
			effectRenderer.addEffect(particle);
		}
	}

	public static void addEntityHoneyDustFX(World world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;
		effectRenderer.addEffect(new ParticleHoneydust(world, x, y, z, 0, 0, 0));
	}

	public static void addClimateParticles(World worldIn, BlockPos pos, Random rand, EnumTemperature temperature, EnumHumidity humidity) {
		if (!shouldSpawnParticle(worldIn)) {
			return;
		}
		if (rand.nextFloat() >= 0.75F) {
			for (int i = 0; i < 3; i++) {
				EnumFacing facing = EnumFacing.HORIZONTALS[rand.nextInt(4)];
				int xOffset = facing.getXOffset();
				int zOffset = facing.getZOffset();
				double x = pos.getX() + 0.5 + (xOffset * 8 + ((1 - MathHelper.abs(xOffset)) * (0.5 - rand.nextFloat()) * 8)) / 16.0;
				double y = pos.getY() + (0.75 + rand.nextFloat() * 14.5) / 16.0;
				double z = pos.getZ() + 0.5 + (zOffset * 8 + ((1 - MathHelper.abs(zOffset)) * (0.5 - rand.nextFloat()) * 8)) / 16.0;
				if (rand.nextBoolean()) {
					ParticleRender.addEntityClimateParticle(worldIn, x, y, z, temperature.color);
				} else {
					ParticleRender.addEntityClimateParticle(worldIn, x, y, z, humidity.color);
				}
			}
		}
	}

	public static void addEntityClimateParticle(World world, double x, double y, double z, int color) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;
		effectRenderer.addEffect(new ParticleClimate(world, x, y, z, color));
	}

	public static void addTransformParticles(World worldIn, BlockPos pos, Random rand) {
		if (!shouldSpawnParticle(worldIn)) {
			return;
		}
		if (rand.nextFloat() >= 0.65F) {
			for (int i = 0; i < 3; i++) {
				EnumFacing facing = EnumFacing.HORIZONTALS[rand.nextInt(4)];
				int xOffset = facing.getXOffset();
				int zOffset = facing.getZOffset();
				double x = pos.getX() + 0.5 + (xOffset * 8 + ((1 - MathHelper.abs(xOffset)) * (0.5 - rand.nextFloat()) * 8)) / 16.0;
				double y = pos.getY() + (0.75 + rand.nextFloat() * 14.5) / 16.0;
				double z = pos.getZ() + 0.5 + (zOffset * 8 + ((1 - MathHelper.abs(zOffset)) * (0.5 - rand.nextFloat()) * 8)) / 16.0;
				ParticleRender.addEntityTransformParticle(worldIn, x, y, z);
			}
		}
	}

	public static void addEntityTransformParticle(World world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;
		effectRenderer.addEffect(new ParticleClimate(world, x, y, z));
	}

	public static void addEntityExplodeFX(World world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;
		Particle Particle = effectRenderer.spawnEffectParticle(EnumParticleTypes.EXPLOSION_NORMAL.getParticleID(), x, y, z, 0, 0, 0);
		effectRenderer.addEffect(Particle);
	}

	public static void addEntitySnowFX(World world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;
		effectRenderer.addEffect(new ParticleSnow(world, x + world.rand.nextGaussian(), y, z + world.rand.nextGaussian()));
	}

	public static void addEntityIgnitionFX(World world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;
		effectRenderer.addEffect(new ParticleIgnition(world, x, y, z));
	}

	public static void addEntitySmokeFX(World world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;
		effectRenderer.addEffect(new ParticleSmoke(world, x, y, z));
	}

	public static void addEntityPotionFX(World world, double x, double y, double z, int color) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;

		ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;
		Particle particle = effectRenderer.spawnEffectParticle(EnumParticleTypes.SPELL.getParticleID(), x, y, z, 0, 0, 0);
		if (particle != null) {
			particle.setRBGColorF(red, green, blue);
			effectRenderer.addEffect(particle);
		}
	}

	public static void addPortalFx(World world, BlockPos pos, Random rand) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		int j = rand.nextInt(2) * 2 - 1;
		int k = rand.nextInt(2) * 2 - 1;
		double xPos = (double) pos.getX() + 0.5D + 0.25D * (double) j;
		double yPos = (double) ((float) pos.getY() + rand.nextFloat());
		double zPos = (double) pos.getZ() + 0.5D + 0.25D * (double) k;
		double xSpeed = (double) (rand.nextFloat() * (float) j);
		double ySpeed = ((double) rand.nextFloat() - 0.5D) * 0.125D;
		double zSpeed = (double) (rand.nextFloat() * (float) k);
		ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;
		Particle particle = effectRenderer.spawnEffectParticle(EnumParticleTypes.PORTAL.getParticleID(), xPos, yPos, zPos, xSpeed, ySpeed, zSpeed);
		if (particle != null) {
			effectRenderer.addEffect(particle);
		}
	}

	public static void addEntityBiodustFX(World world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;
		Particle particle = effectRenderer.spawnEffectParticle(EnumParticleTypes.VILLAGER_HAPPY.ordinal(), x, y, z, 0, 0, 0);
		if (particle != null) {
			effectRenderer.addEffect(particle);
		}
	}
}
