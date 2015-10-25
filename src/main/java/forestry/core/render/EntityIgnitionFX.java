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

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityIgnitionFX extends EntityFX {
	private final float ignitionParticleScale;

	public EntityIgnitionFX(World world, double x, double y, double z) {
		super(world, x, y, z, 0, 0, 0);
		this.motionX *= 0.8;
		this.motionY *= 0.8;
		this.motionZ *= 0.8;
		this.motionY = this.rand.nextFloat() * 0.4F + 0.05F;
		this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
		this.particleScale *= (this.rand.nextFloat() / 2) + 0.3F;
		this.ignitionParticleScale = this.particleScale;
		this.particleMaxAge = (int) (16.0 / (Math.random() * 0.8 + 0.2));
		this.noClip = false;
		this.setParticleTextureIndex(49);
	}

	@Override
	public int getBrightnessForRender(float p_70070_1_) {
		int i = super.getBrightnessForRender(p_70070_1_);
		short short1 = 240;
		int j = i >> 16 & 255;
		return short1 | j << 16;
	}

	/**
	 * Gets how bright this entity is.
	 */
	@Override
	public float getBrightness(float p_70013_1_) {
		return 1.0F;
	}

	@Override
	public void renderParticle(WorldRenderer p_70539_1_, Entity entity, float p_70539_2_, float p_70539_3_,
			float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_) {
		float f6 = (this.particleAge + p_70539_2_) / this.particleMaxAge;
		this.particleScale = this.ignitionParticleScale * (1.0F - f6 * f6);
		super.renderParticle(p_70539_1_, entity, p_70539_2_, p_70539_3_, p_70539_4_, p_70539_5_, p_70539_6_,
				p_70539_7_);
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (this.particleAge++ >= this.particleMaxAge) {
			this.setDead();
		}

		float f = (float) this.particleAge / (float) this.particleMaxAge;

		if (this.rand.nextFloat() > (f * 2)) {
			this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY, this.posZ, this.motionX,
					this.motionY, this.motionZ);
		}

		this.motionY -= 0.03D;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.999D;
		this.motionY *= 0.999D;
		this.motionZ *= 0.999D;

		if (this.onGround) {
			this.motionX *= 0.7;
			this.motionZ *= 0.7;
		}
	}
}