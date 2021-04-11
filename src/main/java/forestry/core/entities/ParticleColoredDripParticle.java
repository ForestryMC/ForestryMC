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
package forestry.core.entities;

//import net.minecraft.block.BlockLiquid;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

//TODO - sort out setParticleTextureIndex
public class ParticleColoredDripParticle extends SpriteTexturedParticle {

	/**
	 * The height of the current bob
	 */
	private int bobTimer;

	public ParticleColoredDripParticle(ClientWorld world, double x, double y, double z, float red, float green, float blue) {
		super(world, x, y, z, 0.0D, 0.0D, 0.0D);
		this.xd = this.yd = this.zd = 0.0D;

		this.rCol = red;
		this.gCol = green;
		this.bCol = blue;

		//		this.setParticleTextureIndex(113);
		this.setSize(0.01F, 0.01F);
		this.gravity = 0.06F;
		this.bobTimer = 40;
		this.lifetime = (int) (64.0D / (Math.random() * 0.8D + 0.2D));
		this.xd = this.yd = this.zd = 0.0D;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		this.yd -= this.gravity;

		if (this.bobTimer-- > 0) {
			this.xd *= 0.02D;
			this.yd *= 0.02D;
			this.zd *= 0.02D;
			//			this.setParticleTextureIndex(113);
		} else {
			//			this.setParticleTextureIndex(112);
		}

		this.move(this.xd, this.yd, this.zd);
		this.xd *= 0.9800000190734863D;
		this.yd *= 0.9800000190734863D;
		this.zd *= 0.9800000190734863D;

		if (this.lifetime-- <= 0) {
			this.remove();
		}

		if (this.onGround) {
			//			this.setParticleTextureIndex(114);

			this.xd *= 0.699999988079071D;
			this.zd *= 0.699999988079071D;
		}

		BlockPos blockpos = new BlockPos(this.x, this.y, this.z);
		BlockState BlockState = this.level.getBlockState(blockpos);
		Material material = BlockState.getMaterial();

		if (material.isLiquid() || material.isSolid()) {
			double d0 = 0.0D;

			//			if (BlockState.getBlock() instanceof BlockLiquid) {
			//				d0 = BlockLiquid.getLiquidHeightPercent(BlockState.getValue(BlockLiquid.LEVEL));
			//			}

			double d1 = MathHelper.floor(this.y) + 1 - d0;

			if (this.y < d1) {
				this.remove();
			}
		}
	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;    //same as DripParticle
	}
}