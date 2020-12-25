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
package forestry.core.render;

import forestry.core.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.DiggingParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ParticleHelper {

    //TODO see ParticleManager.addBlockHitEffects which is almost the same (or maybe should replace this...)
    @OnlyIn(Dist.CLIENT)
    public static boolean addBlockHitEffects(
            World world,
            BlockPos pos,
            Direction side,
            ParticleManager effectRenderer,
            Callback callback
    ) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            float f = 0.1F;
            AxisAlignedBB axisalignedbb = blockState.getShape(world, pos).getBoundingBox();
            double px = x + world.rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - f * 2.0F) + f +
                        axisalignedbb.minX;
            double py = y + world.rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - f * 2.0F) + f +
                        axisalignedbb.minY;
            double pz = z + world.rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - f * 2.0F) + f +
                        axisalignedbb.minZ;
            if (side == Direction.DOWN) {
                py = y + axisalignedbb.minY - f;
            }

            if (side == Direction.UP) {
                py = y + axisalignedbb.maxY + f;
            }

            if (side == Direction.NORTH) {
                pz = z + axisalignedbb.minZ - f;
            }

            if (side == Direction.SOUTH) {
                pz = z + axisalignedbb.maxZ + f;
            }

            if (side == Direction.WEST) {
                px = x + axisalignedbb.minX - f;
            }

            if (side == Direction.EAST) {
                px = x + axisalignedbb.maxX + f;
            }

            //TODO revisit this once I understand particles better
            //         this.addEffect((new DiggingParticle(this.world, d0, d1, d2, 0.0D, 0.0D, 0.0D, blockstate)).setBlockPos(pos).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
            //			DiggingParticle fx = (DiggingParticle) effectRenderer.addEffect(EnumParticleTypes.BLOCK_DUST.getParticleID(), px, py, pz, 0.0D, 0.0D, 0.0D, Block.getStateId(blockState));
            //			DiggingParticle fx = (DiggingParticle) effectRenderer.addEffect(EnumParticleTypes.BLOCK_DUST.getParticleID(), px, py, pz, 0.0D, 0.0D, 0.0D, Block.getStateId(blockState));
            DiggingParticle fx = new DiggingParticle(
                    WorldUtils.asClient(world),
                    px,
                    py,
                    pz,
                    0.0D,
                    0.0D,
                    0.0D,
                    blockState
            );
            effectRenderer.addEffect(fx);
            callback.addHitEffects(fx, world, pos, blockState);
            fx.setBlockPos(new BlockPos(x, y, z)).multiplyVelocity(0.2F).multiplyParticleScaleBy(0.6F);
        }
        return true;
    }

    /**
     * Spawn particles for when the block is destroyed. Due to the nature of how
     * this is invoked, the x/y/z locations are not always guaranteed to host
     * your block. So be sure to do proper sanity checks before assuming that
     * the location is this block.
     *
     * @return True to prevent vanilla break particles from spawning.
     */
    @OnlyIn(Dist.CLIENT)
    public static boolean addDestroyEffects(
            World world,
            Block block,
            BlockState state,
            BlockPos pos,
            ParticleManager effectRenderer,
            Callback callback
    ) {
        if (block != state.getBlock()) {
            return false;
        }

        byte iterations = 4;
        for (int i = 0; i < iterations; ++i) {
            for (int j = 0; j < iterations; ++j) {
                for (int k = 0; k < iterations; ++k) {
                    double px = pos.getX() + (i + 0.5D) / iterations;
                    double py = pos.getY() + (j + 0.5D) / iterations;
                    double pz = pos.getZ() + (k + 0.5D) / iterations;

                    //					DiggingParticle fx = (DiggingParticle) effectRenderer.spawnEffectParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), px, py, pz, px - pos.getX() - 0.5D, py - pos.getY() - 0.5D, pz - pos.getZ() - 0.5D, Block.getStateId(state));
                    //					if (fx != null) {
                    //						callback.addDestroyEffects(fx.setBlockPos(pos), world, pos, state);
                    //					}
                    //TODO correct?
                    //effectRenderer.addBlockDestroyEffects(pos, state);

                }
            }
        }

        return true;
    }

    public interface Callback {

        @OnlyIn(Dist.CLIENT)
        void addHitEffects(DiggingParticle fx, World world, BlockPos pos, BlockState state);

        @OnlyIn(Dist.CLIENT)
        void addDestroyEffects(DiggingParticle fx, World world, BlockPos pos, BlockState state);
    }

    public static class DefaultCallback<B extends Block> implements ParticleHelper.Callback {

        protected final B block;

        public DefaultCallback(B block) {
            this.block = block;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void addHitEffects(DiggingParticle fx, World world, BlockPos pos, BlockState state) {
            setTexture(fx, world, pos, state);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void addDestroyEffects(DiggingParticle fx, World world, BlockPos pos, BlockState state) {
            setTexture(fx, world, pos, state);
        }

        @OnlyIn(Dist.CLIENT)
        protected void setTexture(DiggingParticle fx, World world, BlockPos pos, BlockState state) {
            Minecraft minecraft = Minecraft.getInstance();
            BlockRendererDispatcher blockRendererDispatcher = minecraft.getBlockRendererDispatcher();
            BlockModelShapes blockModelShapes = blockRendererDispatcher.getBlockModelShapes();
            //			fx.sprite = blockModelShapes.getTexture(state);	//TODO At on field SpriteTexturedParticle.sprite but it's protected so idk. Reflection?
        }
    }
}
