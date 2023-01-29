/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser Public License v3 which accompanies this distribution, and is available
 * at http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to: SirSengir (original work), CovertJaguar, Player, Binnie,
 * MysteriousAges
 ******************************************************************************/
package forestry.core.render;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ParticleHelper {

    private static final Random rand = new Random();

    @SideOnly(Side.CLIENT)
    public static boolean addHitEffects(World world, Block block, MovingObjectPosition target,
            EffectRenderer effectRenderer, Callback callback) {
        int x = target.blockX;
        int y = target.blockY;
        int z = target.blockZ;

        int sideHit = target.sideHit;

        if (block != world.getBlock(x, y, z)) {
            return true;
        }

        int meta = world.getBlockMetadata(x, y, z);

        float b = 0.1F;
        double px = x + rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - (b * 2.0F))
                + b
                + block.getBlockBoundsMinX();
        double py = y + rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - (b * 2.0F))
                + b
                + block.getBlockBoundsMinY();
        double pz = z + rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - (b * 2.0F))
                + b
                + block.getBlockBoundsMinZ();

        if (sideHit == 0) {
            py = (double) y + block.getBlockBoundsMinY() - (double) b;
        } else if (sideHit == 1) {
            py = (double) y + block.getBlockBoundsMaxY() + (double) b;
        } else if (sideHit == 2) {
            pz = (double) z + block.getBlockBoundsMinZ() - (double) b;
        } else if (sideHit == 3) {
            pz = (double) z + block.getBlockBoundsMaxZ() + (double) b;
        } else if (sideHit == 4) {
            px = (double) x + block.getBlockBoundsMinX() - (double) b;
        } else if (sideHit == 5) {
            px = (double) x + block.getBlockBoundsMaxX() + (double) b;
        }

        EntityDiggingFX fx = new EntityDiggingFX(world, px, py, pz, 0.0D, 0.0D, 0.0D, block, sideHit, meta);
        callback.addHitEffects(fx, world, x, y, z, meta, sideHit);
        effectRenderer
                .addEffect(fx.applyColourMultiplier(x, y, z).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));

        return true;
    }

    /**
     * Spawn particles for when the block is destroyed. Due to the nature of how this is invoked, the x/y/z locations
     * are not always guaranteed to host your block. So be sure to do proper sanity checks before assuming that the
     * location is this block.
     *
     * @param world          The current world
     * @param x              X position to spawn the particle
     * @param y              Y position to spawn the particle
     * @param z              Z position to spawn the particle
     * @param meta           The metadata for the block before it was destroyed.
     * @param effectRenderer A reference to the current effect renderer.
     * @param callback
     * @return True to prevent vanilla break particles from spawning.
     */
    @SideOnly(Side.CLIENT)
    public static boolean addDestroyEffects(World world, Block block, int x, int y, int z, int meta,
            EffectRenderer effectRenderer, Callback callback) {
        if (block != world.getBlock(x, y, z)) {
            return true;
        }

        byte iterations = 4;
        for (int i = 0; i < iterations; ++i) {
            for (int j = 0; j < iterations; ++j) {
                for (int k = 0; k < iterations; ++k) {
                    double px = x + (i + 0.5D) / (double) iterations;
                    double py = y + (j + 0.5D) / (double) iterations;
                    double pz = z + (k + 0.5D) / (double) iterations;
                    int random = rand.nextInt(6);

                    EntityDiggingFX fx = new EntityDiggingFX(
                            world,
                            px,
                            py,
                            pz,
                            px - x - 0.5D,
                            py - y - 0.5D,
                            pz - z - 0.5D,
                            block,
                            random,
                            meta);
                    callback.addDestroyEffects(fx, world, x, y, z, meta);
                    effectRenderer.addEffect(fx.applyColourMultiplier(x, y, z));
                }
            }
        }

        return true;
    }

    public interface Callback {

        @SideOnly(Side.CLIENT)
        void addHitEffects(EntityDiggingFX fx, World world, int x, int y, int z, int meta, int side);

        @SideOnly(Side.CLIENT)
        void addDestroyEffects(EntityDiggingFX fx, World world, int x, int y, int z, int meta);
    }

    public static class DefaultCallback implements ParticleHelper.Callback {

        private final Block block;

        public DefaultCallback(Block block) {
            this.block = block;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void addHitEffects(EntityDiggingFX fx, World world, int x, int y, int z, int meta, int side) {
            setTexture(fx, world, x, y, z, side);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void addDestroyEffects(EntityDiggingFX fx, World world, int x, int y, int z, int meta) {
            setTexture(fx, world, x, y, z, 0);
        }

        @SideOnly(Side.CLIENT)
        private void setTexture(EntityDiggingFX fx, World world, int x, int y, int z, int side) {
            fx.setParticleIcon(block.getIcon(world, x, y, z, side));
        }
    }
}
