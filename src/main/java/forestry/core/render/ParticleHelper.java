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

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.proxy.Proxies;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ParticleHelper {

	@SideOnly(Side.CLIENT)
	public static boolean addBlockHitEffects(World world, BlockPos pos, EnumFacing side, ParticleManager effectRenderer, Callback callback) {
		IBlockState iblockstate = world.getBlockState(pos);
		if(iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			float f = 0.1F;
			AxisAlignedBB axisalignedbb = iblockstate.getBoundingBox(world, pos);
			double px = x + world.rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - f * 2.0F) + f + axisalignedbb.minX;
			double py = y + world.rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - f * 2.0F) + f + axisalignedbb.minY;
			double pz = z + world.rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - f * 2.0F) + f + axisalignedbb.minZ;
			if(side == EnumFacing.DOWN) {
				py = y + axisalignedbb.minY - f;
			}

			if(side == EnumFacing.UP) {
				py = y + axisalignedbb.maxY + f;
			}

			if(side == EnumFacing.NORTH) {
				pz = z + axisalignedbb.minZ - f;
			}

			if(side == EnumFacing.SOUTH) {
				pz = z + axisalignedbb.maxZ + f;
			}

			if(side == EnumFacing.WEST) {
				px = x + axisalignedbb.minX - f;
			}

			if(side == EnumFacing.EAST) {
				px = x + axisalignedbb.maxX + f;
			}

			ParticleDigging fx = (ParticleDigging) effectRenderer.spawnEffectParticle(EnumParticleTypes.BLOCK_DUST.getParticleID(), px, py, pz, 0.0D, 0.0D, 0.0D, Block.getStateId(iblockstate));
			callback.addHitEffects(fx, world, pos, iblockstate);
			effectRenderer.addEffect(fx.setBlockPos(new BlockPos(x, y, z)).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
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
	@SideOnly(Side.CLIENT)
	public static boolean addDestroyEffects(World world, Block block, IBlockState state, BlockPos pos, ParticleManager effectRenderer, Callback callback) {
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

					ParticleDigging fx = (ParticleDigging) effectRenderer.spawnEffectParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), px, py, pz, px - pos.getX() - 0.5D, py - pos.getY() - 0.5D, pz - pos.getZ() - 0.5D, Block.getStateId(state));
					callback.addDestroyEffects(fx, world, pos, state);
					effectRenderer.addEffect(fx.setBlockPos(pos));
				}
			}
		}

		return true;
	}

	public interface Callback {

		@SideOnly(Side.CLIENT)
		void addHitEffects(ParticleDigging fx, World world, BlockPos pos, IBlockState state);

		@SideOnly(Side.CLIENT)
		void addDestroyEffects(ParticleDigging fx, World world, BlockPos pos, IBlockState state);
	}

	public static class DefaultCallback<B extends Block> implements ParticleHelper.Callback {

		protected final B block;

		public DefaultCallback(B block) {
			this.block = block;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void addHitEffects(ParticleDigging fx, World world, BlockPos pos, IBlockState state) {
			setTexture(fx, world, pos, state);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void addDestroyEffects(ParticleDigging fx, World world, BlockPos pos, IBlockState state) {
			setTexture(fx, world, pos, state);
		}

		@SideOnly(Side.CLIENT)
		protected void setTexture(ParticleDigging fx, World world, BlockPos pos, IBlockState state) {
			Minecraft minecraft = Proxies.common.getClientInstance();
			BlockRendererDispatcher blockRendererDispatcher = minecraft.getBlockRendererDispatcher();
			BlockModelShapes blockModelShapes = blockRendererDispatcher.getBlockModelShapes();
			TextureAtlasSprite texture = blockModelShapes.getTexture(state);
			fx.setParticleTexture(texture);
		}
	}
}
