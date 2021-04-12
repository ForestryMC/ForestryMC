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
package forestry.core.particles;

import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//TODO - sort out setParticleTextureIndex
public class ColoredDripParticle extends SpriteTexturedParticle {
	private final Fluid type;

	public ColoredDripParticle(World world, double x, double y, double z, float red, float green, float blue, Fluid type) {
		super((ClientWorld) world, x, y, z, 0.0D, 0.0D, 0.0D);
		this.xd = this.yd = this.zd = 0.0D;

		this.rCol = red;
		this.gCol = green;
		this.bCol = blue;

		//		this.setParticleTextureIndex(113);
		this.setSize(0.01F, 0.01F);
		this.gravity = 0.06F;
		this.lifetime = (int) (64.0D / (Math.random() * 0.8D + 0.2D));
		this.xd = this.yd = this.zd = 0.0D;
		this.type = type;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		this.preMoveUpdate();
		if (!this.removed) {
			this.yd -= (double) this.gravity;
			this.move(this.xd, this.yd, this.zd);
			this.postMoveUpdate();
			if (!this.removed) {
				this.xd *= (double) 0.98F;
				this.yd *= (double) 0.98F;
				this.zd *= (double) 0.98F;
				BlockPos blockpos = new BlockPos(this.x, this.y, this.z);
				FluidState fluidstate = this.level.getFluidState(blockpos);
				if (fluidstate.getType() == this.type && this.y < (double) ((float) blockpos.getY() + fluidstate.getHeight(this.level, blockpos))) {
					this.remove();
				}

			}
		}
		/*this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		this.motionY -= this.particleGravity;

		if (this.bobTimer-- > 0) {
			this.motionX *= 0.02D;
			this.motionY *= 0.02D;
			this.motionZ *= 0.02D;
			//			this.setParticleTextureIndex(113);
		} else {
			//			this.setParticleTextureIndex(112);
		}

		this.move(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.9800000190734863D;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= 0.9800000190734863D;

		if (this.maxAge-- <= 0) {
			this.setExpired();
		}

		if (this.onGround) {
			//			this.setParticleTextureIndex(114);

			this.motionX *= 0.699999988079071D;
			this.motionZ *= 0.699999988079071D;
		}

		BlockPos blockpos = new BlockPos(this.posX, this.posY, this.posZ);
		BlockState BlockState = this.world.getBlockState(blockpos);
		Material material = BlockState.getMaterial();

		if (material.isLiquid() || material.isSolid()) {
			double d0 = 0.0D;

			//			if (BlockState.getBlock() instanceof BlockLiquid) {
			//				d0 = BlockLiquid.getLiquidHeightPercent(BlockState.getValue(BlockLiquid.LEVEL));
			//			}

			double d1 = MathHelper.floor(this.posY) + 1 - d0;

			if (this.posY < d1) {
				this.setExpired();
			}
		}*/
	}

	protected void preMoveUpdate() {
		if (this.lifetime-- <= 0) {
			this.remove();
		}

	}

	protected void postMoveUpdate() {
	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;    //same as DripParticle
	}
}