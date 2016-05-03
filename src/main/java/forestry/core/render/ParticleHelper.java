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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.proxy.Proxies;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ParticleHelper {

	private static final Random rand = new Random();

	@SideOnly(Side.CLIENT)
	public static boolean addHitEffects(World world, Block block, RayTraceResult target, EffectRenderer effectRenderer, Callback callback) {
		int x = target.getBlockPos().getX();
		int y = target.getBlockPos().getY();
		int z = target.getBlockPos().getZ();

		EnumFacing sideHit = target.sideHit;
		
		IBlockState state = world.getBlockState(target.getBlockPos());

		if (block != state.getBlock()) {
			return true;
		}

		float b = 0.1F;
		double px = x + rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - b * 2.0F) + b + block.getBlockBoundsMinX();
		double py = y + rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - b * 2.0F) + b + block.getBlockBoundsMinY();
		double pz = z + rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - b * 2.0F) + b + block.getBlockBoundsMinZ();

		if (sideHit == EnumFacing.DOWN) {
			py = y + block.getBlockBoundsMinY() - b;
		} else if (sideHit == EnumFacing.UP) {
			py = y + block.getBlockBoundsMaxY() + b;
		} else if (sideHit == EnumFacing.NORTH) {
			pz = z + block.getBlockBoundsMinZ() - b;
		} else if (sideHit == EnumFacing.SOUTH) {
			pz = z + block.getBlockBoundsMaxZ() + b;
		} else if (sideHit == EnumFacing.WEST) {
			px = x + block.getBlockBoundsMinX() - b;
		} else if (sideHit == EnumFacing.EAST) {
			px = x + block.getBlockBoundsMaxX() + b;
		}

		EntityDiggingFX fx = (EntityDiggingFX) effectRenderer.spawnEffectParticle(EnumParticleTypes.BLOCK_DUST.getParticleID(), px, py, pz, 0.0D, 0.0D, 0.0D, Block.getStateId(state));
		callback.addHitEffects(fx, target.getBlockPos(), state);
		effectRenderer.addEffect(fx.setBlockPos(new BlockPos(x, y, z)).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));

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
	@SideOnly(Side.CLIENT)
	public static boolean addDestroyEffects(World world, Block block, IBlockState state, BlockPos pos, EffectRenderer effectRenderer, Callback callback) {
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
					int random = rand.nextInt(6);

					EntityDiggingFX fx = (EntityDiggingFX) effectRenderer.spawnEffectParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), px, py, pz, px - pos.getX() - 0.5D, py - pos.getY() - 0.5D, pz - pos.getZ() - 0.5D, Block.getStateId(state));
					callback.addDestroyEffects(fx, pos, state);
					effectRenderer.addEffect(fx.setBlockPos(pos));
				}
			}
		}

		return true;
	}

	public interface Callback {

		@SideOnly(Side.CLIENT)
		void addHitEffects(EntityDiggingFX fx, BlockPos pos, IBlockState state);

		@SideOnly(Side.CLIENT)
		void addDestroyEffects(EntityDiggingFX fx, BlockPos pos, IBlockState state);
	}

	public static class DefaultCallback<B extends Block> implements ParticleHelper.Callback {

		protected final B block;

		public DefaultCallback(B block) {
			this.block = block;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void addHitEffects(EntityDiggingFX fx, BlockPos pos, IBlockState state) {
			setTexture(fx, pos, state);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void addDestroyEffects(EntityDiggingFX fx, BlockPos pos, IBlockState state) {
			setTexture(fx, pos, state);
		}

		@SideOnly(Side.CLIENT)
		protected void setTexture(EntityDiggingFX fx, BlockPos pos, IBlockState state) {
			fx.setParticleIcon(Proxies.common.getClientInstance().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state));
		}
	}
}
