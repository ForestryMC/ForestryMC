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
package forestry.apiculture.entities;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class EntityFXBee extends EntityFX {
	public EntityFXBee(World world, double x, double y, double z, int color) {
		super(world, x, y, z, 0.0D, 0.0D, 0.0D);

		particleRed = (color >> 16 & 255) / 255.0F;
		particleGreen = (color >> 8 & 255) / 255.0F;
		particleBlue = (color & 255) / 255.0F;

		this.setSize(0.1F, 0.1F);
		this.particleScale *= 0.2F;
		this.particleMaxAge = (int) (20.0D / (Math.random() * 0.8D + 0.2D));
		this.noClip = true;

		this.motionX *= 0.1D;
		this.motionY *= 0.015D;
		this.motionZ *= 0.1D;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= (1 + 0.2D * rand.nextFloat());
		this.motionY = (this.motionY + 0.2 * (-0.5 + rand.nextFloat())) / 2;
		this.motionZ *= (1 + 0.2D * rand.nextFloat());

		if (this.particleAge++ >= this.particleMaxAge) {
			this.setDead();
		}
	}

	@Override
	public void renderParticle(Tessellator tessellator, float f, float f1, float f2, float f3, float f4, float f5) {
		float minU = 0;
		float maxU = 1;
		float minV = 0;
		float maxV = 1;

		if (this.particleIcon != null) {
			minU = particleIcon.getMinU();
			maxU = particleIcon.getMaxU();
			minV = particleIcon.getMinV();
			maxV = particleIcon.getMaxV();
		}

		float f10 = 0.1F * particleScale;
		float f11 = (float) ((prevPosX + (posX - prevPosX) * f) - interpPosX);
		float f12 = (float) ((prevPosY + (posY - prevPosY) * f) - interpPosY);
		float f13 = (float) ((prevPosZ + (posZ - prevPosZ) * f) - interpPosZ);

		tessellator.setColorRGBA_F(particleRed, particleGreen, particleBlue, 1.0F);
		tessellator.addVertexWithUV(f11 - f1 * f10 - f4 * f10, f12 - f2 * f10, f13 - f3 * f10 - f5 * f10, maxU, maxV);
		tessellator.addVertexWithUV((f11 - f1 * f10) + f4 * f10, f12 + f2 * f10, (f13 - f3 * f10) + f5 * f10, maxU, minV);
		tessellator.addVertexWithUV(f11 + f1 * f10 + f4 * f10, f12 + f2 * f10, f13 + f3 * f10 + f5 * f10, minU, minV);
		tessellator.addVertexWithUV((f11 + f1 * f10) - f4 * f10, f12 - f2 * f10, (f13 + f3 * f10) - f5 * f10, minU, maxV);
	}

	@Override
	public int getBrightnessForRender(float partialTicks) {
		return 0x0000f0;
	}

	@Override
	public int getFXLayer() {
		return 2;
	}
}
