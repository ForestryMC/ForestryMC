package forestry.core.render;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.hives.IHiveTile;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.apiculture.genetics.alleles.AlleleEffect;
import forestry.apiculture.particles.BeeParticleData;
import forestry.core.config.Config;
import forestry.core.particles.IgnitionParticle;
import forestry.core.particles.SmokeParticle;
import forestry.core.particles.SnowParticleData;
import forestry.core.utils.ColourUtil;
import forestry.core.utils.VectUtil;
import forestry.core.utils.WorldUtils;
import genetics.api.individual.IGenome;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class ParticleRender {
    public static boolean shouldSpawnParticle(World world) {
        if (!Config.enableParticleFX) {
            return false;
        }

        Minecraft mc = Minecraft.getInstance();
        ParticleStatus particleSetting = mc.gameSettings.particles;

        if (particleSetting == ParticleStatus.MINIMAL) {
            return world.rand.nextInt(10) == 0;
        } else if (particleSetting == ParticleStatus.DECREASED) {
            return world.rand.nextInt(3) != 0;
        }

        // all
        return true;
    }

    public static void addBeeHiveFX(IBeeHousing housing, IGenome genome, List<BlockPos> flowerPositions) {
        ClientWorld world = WorldUtils.asClient(housing.getWorldObj());
        if (!shouldSpawnParticle(world)) {
            return;
        }

        Vector3d particleStart = housing.getBeeFXCoordinates();

        // Avoid rendering bee particles that are too far away, they're very small.
        // At 32+ distance, have no bee particles. Make more particles up close.
        BlockPos playerPosition = Minecraft.getInstance().player.getPosition();
        double playerDistanceSq = playerPosition.distanceSq(new Vector3i(
                particleStart.x,
                particleStart.y,
                particleStart.z
        ));
        if (world.rand.nextInt(1024) < playerDistanceSq) {
            return;
        }

        int color = genome.getActiveAllele(BeeChromosomes.SPECIES).getSpriteColour(0);

        int randomInt = world.rand.nextInt(100);

        if (housing instanceof IHiveTile) {
            if (((IHiveTile) housing).isAngry() || randomInt >= 85) {
                List<LivingEntity> entitiesInRange = AlleleEffect.getEntitiesInRange(
                        genome,
                        housing,
                        LivingEntity.class
                );
                if (!entitiesInRange.isEmpty()) {
                    LivingEntity entity = entitiesInRange.get(world.rand.nextInt(entitiesInRange.size()));
                    world.addParticle(
                            new BeeParticleData(
                                    particleStart.getX(),
                                    particleStart.getY(),
                                    particleStart.getZ(),
                                    entity.getPosition().toLong(),
                                    color
                            ),
                            particleStart.getX(),
                            particleStart.getY(),
                            particleStart.getZ(),
                            0.0f,
                            0.0f,
                            0.0f
                    );
                    return;
                }
            }
        }

        if (randomInt < 75 && !flowerPositions.isEmpty()) {
            BlockPos destination = flowerPositions.get(world.rand.nextInt(flowerPositions.size()));
            world.addParticle(
                    new BeeParticleData(
                            particleStart.getX(),
                            particleStart.getY(),
                            particleStart.getZ(),
                            destination.toLong(),
                            color
                    ),
                    particleStart.getX(),
                    particleStart.getY(),
                    particleStart.getZ(),
                    0.0f,
                    0.0f,
                    0.0f
            );
        } else {
            Vector3i area = AlleleEffect.getModifiedArea(genome, housing);
            Vector3i offset = housing.getCoordinates().add(-area.getX() / 2, -area.getY() / 4, -area.getZ() / 2);
            BlockPos destination = VectUtil.getRandomPositionInArea(world.rand, area).add(offset);
            world.addParticle(
                    new BeeParticleData(
                            particleStart.getX(),
                            particleStart.getY(),
                            particleStart.getZ(),
                            destination.toLong(),
                            color
                    ),
                    particleStart.getX(),
                    particleStart.getY(),
                    particleStart.getZ(),
                    0.0f,
                    0.0f,
                    0.0f
            );
        }
    }

    public static void addEntityHoneyDustFX(World world, double x, double y, double z) {
        if (!shouldSpawnParticle(world)) {
            return;
        }

        float particleRed = 0.9F + world.rand.nextFloat() * 0.1F;
        float particleGreen = 0.75F + world.rand.nextFloat() * 0.2F;
        float particleBlue = 0F + world.rand.nextFloat() * 0.2F;

        world.addParticle(
                new RedstoneParticleData(particleRed, particleGreen, particleBlue, 1.0F),
                x,
                y,
                z,
                0,
                0,
                0
        );
    }

    public static void addClimateParticles(
            World worldIn,
            BlockPos pos,
            Random rand,
            EnumTemperature temperature,
            EnumHumidity humidity
    ) {
        if (!shouldSpawnParticle(worldIn)) {
            return;
        }
        if (rand.nextFloat() >= 0.75F) {
            for (int i = 0; i < 3; i++) {
                Direction facing = Direction.Plane.HORIZONTAL.random(rand);
                int xOffset = facing.getXOffset();
                int zOffset = facing.getZOffset();
                double x = pos.getX() + 0.5 +
                           (xOffset * 8 + ((1 - MathHelper.abs(xOffset)) * (0.5 - rand.nextFloat()) * 8)) / 16.0;
                double y = pos.getY() + (0.75 + rand.nextFloat() * 14.5) / 16.0;
                double z = pos.getZ() + 0.5 +
                           (zOffset * 8 + ((1 - MathHelper.abs(zOffset)) * (0.5 - rand.nextFloat()) * 8)) / 16.0;
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

        float particleRed = ColourUtil.getRedAsFloat(color);
        float particleGreen = ColourUtil.getGreenAsFloat(color);
        float particleBlue = ColourUtil.getBlueAsFloat(color);

        world.addParticle(
                new RedstoneParticleData(particleRed, particleGreen, particleBlue, 1.0F),
                x,
                y,
                z,
                0,
                0,
                0
        );
    }

    public static void addTransformParticles(World worldIn, BlockPos pos, Random rand) {
        if (!shouldSpawnParticle(worldIn)) {
            return;
        }
        if (rand.nextFloat() >= 0.65F) {
            for (int i = 0; i < 3; i++) {
                Direction facing = Direction.Plane.HORIZONTAL.random(rand);
                int xOffset = facing.getXOffset();
                int zOffset = facing.getZOffset();
                double x = pos.getX() + 0.5 +
                           (xOffset * 8 + ((1 - MathHelper.abs(xOffset)) * (0.5 - rand.nextFloat()) * 8)) / 16.0;
                double y = pos.getY() + (0.75 + rand.nextFloat() * 14.5) / 16.0;
                double z = pos.getZ() + 0.5 +
                           (zOffset * 8 + ((1 - MathHelper.abs(zOffset)) * (0.5 - rand.nextFloat()) * 8)) / 16.0;
                ParticleRender.addEntityTransformParticle(worldIn, x, y, z);
            }
        }
    }

    public static void addEntityTransformParticle(World world, double x, double y, double z) {
        if (!shouldSpawnParticle(world)) {
            return;
        }

        int color = 0x37485a;
        float particleRed = ColourUtil.getRedAsFloat(color);
        float particleGreen = ColourUtil.getGreenAsFloat(color);
        float particleBlue = ColourUtil.getBlueAsFloat(color);

        world.addParticle(
                new RedstoneParticleData(particleRed, particleGreen, particleBlue, 1.0F),
                x,
                y,
                z,
                0,
                0,
                0
        );
    }

    public static void addEntityExplodeFX(World world, double x, double y, double z) {
        if (!shouldSpawnParticle(world)) {
            return;
        }

        world.addParticle(RedstoneParticleData.REDSTONE_DUST, x, y, z, 0, 0, 0);
    }

    public static void addEntitySnowFX(World world, double x, double y, double z) {
        if (!shouldSpawnParticle(world)) {
            return;
        }

        world.addParticle(
                new SnowParticleData(
                        x + world.rand.nextGaussian(),
                        y,
                        z + world.rand.nextGaussian()
                ),
                x + world.rand.nextGaussian(),
                y,
                z + world.rand.nextGaussian(),
                0.0f,
                0.0f,
                0.0f
        );
    }

    public static void addEntityIgnitionFX(ClientWorld world, double x, double y, double z) {
        if (!shouldSpawnParticle(world)) {
            return;
        }

        ParticleManager effectRenderer = Minecraft.getInstance().particles;
        effectRenderer.addEffect(new IgnitionParticle(world, x, y, z));
    }

    public static void addEntitySmokeFX(World world, double x, double y, double z) {
        if (!shouldSpawnParticle(world)) {
            return;
        }

        ParticleManager effectRenderer = Minecraft.getInstance().particles;
        effectRenderer.addEffect(new SmokeParticle(WorldUtils.asClient(world), x, y, z));
    }

    public static void addEntityPotionFX(World world, double x, double y, double z, int color) {
        if (!shouldSpawnParticle(world)) {
            return;
        }

        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;

        world.addParticle(
                new RedstoneParticleData(red, green, blue, 1.0F),
                x,
                y,
                z,
                0,
                0,
                0
        );
    }

    public static void addPortalFx(World world, BlockPos pos, Random rand) {
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

        world.addParticle(
                RedstoneParticleData.REDSTONE_DUST,
                xPos,
                yPos,
                zPos,
                xSpeed,
                ySpeed,
                zSpeed
        );
    }
}
