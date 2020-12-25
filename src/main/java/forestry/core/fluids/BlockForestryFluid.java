/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.core.fluids;

import forestry.core.particles.ColoredDripParticle;
import forestry.modules.features.FeatureFluid;
import forestry.modules.features.FluidProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.awt.*;
import java.util.Random;

public class BlockForestryFluid extends FlowingFluidBlock {

    private final FeatureFluid feature;
    private final boolean flammable;
    private final int flammability;
    private final Color color;

    public BlockForestryFluid(FeatureFluid feature) {
        super(
                feature.fluid(),
                Block.Properties.create(feature.fluid()
                                               .getAttributes()
                                               .getTemperature() > 505 ? Material.LAVA : Material.WATER)
                                .doesNotBlockMovement()
                                .hardnessAndResistance(100.0F).noDrops()
        );
        this.feature = feature;

        FluidProperties properties = feature.getProperties();
        this.flammability = properties.flammability;
        this.flammable = properties.flammable;

        this.color = properties.particleColor;
    }

    private static boolean isFlammable(IBlockReader world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        return blockState.isFlammable(world, pos, Direction.UP);
    }

    private static boolean isNeighborFlammable(World world, int x, int y, int z) {
        return isFlammable(world, new BlockPos(x - 1, y, z)) ||
               isFlammable(world, new BlockPos(x + 1, y, z)) ||
               isFlammable(world, new BlockPos(x, y, z - 1)) ||
               isFlammable(world, new BlockPos(x, y, z + 1)) ||
               isFlammable(world, new BlockPos(x, y - 1, z)) ||
               isFlammable(world, new BlockPos(x, y + 1, z));
    }

    private static boolean isNearFire(World world, int x, int y, int z) {
        AxisAlignedBB boundingBox = new AxisAlignedBB(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
        // Copied from 'Entity.move', replaces method 'World.isFlammableWithin'
        return BlockPos.getAllInBox(boundingBox.shrink(0.001D)).noneMatch((pos) -> {
            BlockState state = world.getBlockState(pos);
            return state.isIn(BlockTags.FIRE) || state.isIn(Blocks.LAVA) || state.isBurning(world, pos);
        });
    }

    @Override
    public void randomTick(BlockState blockState, ServerWorld world, BlockPos pos, Random rand) {
        double d0 = pos.getX();
        double d1 = pos.getY();
        double d2 = pos.getZ();

        if (this.material == Material.WATER) {
            int i = blockState.get(LEVEL);

            if (i > 0 && i < 8) {
                if (getFluid().getAttributes().getViscosity(world, pos) < 5000 && rand.nextInt(64) == 0) {
                    world.playSound(
                            d0 + 0.5D,
                            d1 + 0.5D,
                            d2 + 0.5D,
                            SoundEvents.BLOCK_WATER_AMBIENT,
                            SoundCategory.BLOCKS,
                            rand.nextFloat() * 0.25F + 0.75F,
                            rand.nextFloat() + 0.5F,
                            false
                    );
                }
            } else if (rand.nextInt(10) == 0) {
                world.addParticle(
                        ParticleTypes.UNDERWATER,
                        d0 + rand.nextFloat(),
                        d1 + rand.nextFloat(),
                        d2 + rand.nextFloat(),
                        0.0D,
                        0.0D,
                        0.0D
                );
            }
        }

        if (this.material == Material.LAVA
            && world.getBlockState(pos.up()).getMaterial() == Material.AIR
            && !world.getBlockState(pos.up()).isOpaqueCube(world, pos.up())
        ) {
            if (rand.nextInt(100) == 0) {
                double d8 = d0 + rand.nextFloat();
                double d4 = d1 + 1;
                double d6 = d2 + rand.nextFloat();
                world.addParticle(ParticleTypes.LAVA, d8, d4, d6, 0.0D, 0.0D, 0.0D);
                world.playSound(
                        d8,
                        d4,
                        d6,
                        SoundEvents.BLOCK_LAVA_POP,
                        SoundCategory.BLOCKS,
                        0.2F + rand.nextFloat() * 0.2F,
                        0.9F + rand.nextFloat() * 0.15F,
                        false
                );
            }

            if (rand.nextInt(200) == 0) {
                world.playSound(
                        d0,
                        d1,
                        d2,
                        SoundEvents.BLOCK_LAVA_AMBIENT,
                        SoundCategory.BLOCKS,
                        0.2F + rand.nextFloat() * 0.2F,
                        0.9F + rand.nextFloat() * 0.15F,
                        false
                );
            }
        }

        if (rand.nextInt(10) == 0 && Block.hasEnoughSolidSide(world, pos.down(), Direction.DOWN)) {
            Material material = world.getBlockState(pos.down(2)).getMaterial();

            if (!material.blocksMovement() && !material.isLiquid()) {
                double px = d0 + rand.nextFloat();
                double py = d1 - 1.05D;
                double pz = d2 + rand.nextFloat();

                Particle fx = new ColoredDripParticle(
                        (World) world,
                        px,
                        py,
                        pz,
                        color.getRed() / 255f,
                        color.getGreen() / 255f,
                        color.getBlue() / 255f
                );
                Minecraft.getInstance().particles.addEffect(fx);
            }
        }
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return flammable ? 30 : 0;
    }

    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return flammability;
    }

    @Override
    public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return flammable;
    }

    @Override
    public boolean isFireSource(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
        return flammable && flammability == 0;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        super.tick(state, world, pos, rand);

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        // Start fires if the fluid is lava-like
        if (material == Material.LAVA) {
            int rangeUp = rand.nextInt(3);

            for (int i = 0; i < rangeUp; ++i) {
                x += rand.nextInt(3) - 1;
                ++y;
                z += rand.nextInt(3) - 1;
                BlockState blockState = world.getBlockState(new BlockPos(x, y, z));
                if (blockState.getMaterial() == Material.AIR) {
                    if (isNeighborFlammable(world, x, y, z)) {
                        world.setBlockState(new BlockPos(x, y, z), Blocks.FIRE.getDefaultState());
                        return;
                    }
                } else if (blockState.getMaterial().blocksMovement()) {
                    return;
                }
            }

            if (rangeUp == 0) {
                int startX = x;
                int startZ = z;

                for (int i = 0; i < 3; ++i) {
                    x = startX + rand.nextInt(3) - 1;
                    z = startZ + rand.nextInt(3) - 1;

                    BlockPos posAbove = new BlockPos(pos.getX(), y + 1, z);
                    if (world.isAirBlock(posAbove) && isFlammable(world, new BlockPos(x, y, z))) {
                        world.setBlockState(posAbove, Blocks.FIRE.getDefaultState());
                    }
                }
            }
        }

        // explode if very flammable and near fire
        int flammability = getFlammability(state, world, pos, null);
        if (flammability > 0) {
            // Explosion size is determined by flammability, up to size 4.
            float explosionSize = 4F * flammability / 300F;
            if (explosionSize > 1.0 && isNearFire(world, pos.getX(), pos.getY(), pos.getZ())) {
                world.setBlockState(pos, Blocks.FIRE.getDefaultState());
                world.createExplosion(
                        null,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        explosionSize,
                        true,
                        Explosion.Mode.DESTROY
                );
            }
        }
    }
}
