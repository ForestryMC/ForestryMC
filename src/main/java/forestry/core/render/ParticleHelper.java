/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.render;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ParticleHelper {

	private static final Random rand = new Random();

	@SideOnly(Side.CLIENT)
	public static boolean addHitEffects(World world, Block block, MovingObjectPosition target,
			EffectRenderer effectRenderer, ParticleHelperCallback callback) {

		EnumFacing sideHit = target.sideHit;

		BlockPos pos = target.getBlockPos();
		if (block != world.getBlockState(pos).getBlock()) {
			return true;
		}

		IBlockState state = world.getBlockState(pos);
		int meta = block.getMetaFromState(state);

		float b = 0.1F;
		double px = pos.getX()
				+ rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - (b * 2.0F)) + b
				+ block.getBlockBoundsMinX();
		double py = pos.getY()
				+ rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - (b * 2.0F)) + b
				+ block.getBlockBoundsMinY();
		double pz = pos.getZ()
				+ rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - (b * 2.0F)) + b
				+ block.getBlockBoundsMinZ();

		if (sideHit == EnumFacing.DOWN) {
			py = pos.getY() + block.getBlockBoundsMinY() - b;
		}

		if (sideHit == EnumFacing.UP) {
			py = pos.getY() + block.getBlockBoundsMaxY() + b;
		}

		if (sideHit == EnumFacing.NORTH) {
			pz = pos.getZ() + block.getBlockBoundsMinZ() - b;
		}

		if (sideHit == EnumFacing.SOUTH) {
			pz = pos.getZ() + block.getBlockBoundsMaxZ() + b;
		}

		if (sideHit == EnumFacing.WEST) {
			px = pos.getX() + block.getBlockBoundsMinX() - b;
		}

		if (sideHit == EnumFacing.EAST) {
			px = pos.getX() + block.getBlockBoundsMaxX() + b;
		}

		EntityFX fx = effectRenderer.spawnEffectParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), px, py, pz,
				px - pos.getX() - 0.5D, py - pos.getY() - 0.5D, pz - pos.getZ() - 0.5D, Block.getIdFromBlock(block));

		if (callback != null) {
			callback.addHitEffects((EntityDiggingFX) fx, world, pos.getX(), pos.getY(), pos.getZ(), meta);
		}

		effectRenderer.addEffect(
				((EntityDiggingFX) fx).func_174846_a(pos).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));

		return true;
	}

	/**
	 * Spawn particles for when the block is destroyed. Due to the nature of how
	 * this is invoked, the x/y/z locations are not always guaranteed to host
	 * your block. So be sure to do proper sanity checks before assuming that
	 * the location is this block.
	 *
	 * @param world
	 *            The current world
	 * @param block
	 * @param x
	 *            X position to spawn the particle
	 * @param y
	 *            Y position to spawn the particle
	 * @param z
	 *            Z position to spawn the particle
	 * @param meta
	 *            The metadata for the block before it was destroyed.
	 * @param effectRenderer
	 *            A reference to the current effect renderer.
	 * @param callback
	 * @return True to prevent vanilla break particles from spawning.
	 */
	@SideOnly(Side.CLIENT)
	public static boolean addDestroyEffects(World world, Block block, BlockPos pos, IBlockState state,
			EffectRenderer effectRenderer, ParticleHelperCallback callback) {
		if (block != state.getBlock()) {
			return true;
		}

		byte iterations = 4;
		for (int i = 0; i < iterations; ++i) {
			for (int j = 0; j < iterations; ++j) {
				for (int k = 0; k < iterations; ++k) {
					double px = pos.getX() + (i + 0.5D) / iterations;
					double py = pos.getY() + (j + 0.5D) / iterations;
					double pz = pos.getZ() + (k + 0.5D) / iterations;
					int random = world.rand.nextInt(6);
					EntityFX fx = effectRenderer.spawnEffectParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), px,
							py, pz, px - pos.getX() - 0.5D, py - pos.getY() - 0.5D, pz - pos.getZ() - 0.5D,
							Block.getIdFromBlock(block));

					if (callback != null) {
						callback.addDestroyEffects((EntityDiggingFX) fx, world, pos.getX(), pos.getY(), pos.getZ(),
								block.getMetaFromState(state));
					}

					effectRenderer.addEffect(((EntityDiggingFX) fx).func_174846_a(pos));
				}
			}
		}

		return true;
	}

}
