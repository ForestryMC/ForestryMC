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

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import forestry.apiculture.ModuleApiculture;

public class ParticleBeeExplore extends Particle {
	private final Vec3d origin;

	public ParticleBeeExplore(World world, Vec3d origin, BlockPos destination, int color) {
		super(world, origin.x, origin.y, origin.z, 0.0D, 0.0D, 0.0D);
		setParticleTexture(ModuleApiculture.getBeeSprite());
		this.origin = origin;

		this.motionX = (destination.getX() + 0.5 - this.posX) * 0.015;
		this.motionY = (destination.getY() + 0.5 - this.posY) * 0.015;
		this.motionZ = (destination.getZ() + 0.5 - this.posZ) * 0.015;

		particleRed = (color >> 16 & 255) / 255.0F;
		particleGreen = (color >> 8 & 255) / 255.0F;
		particleBlue = (color & 255) / 255.0F;

		this.setSize(0.1F, 0.1F);
		this.particleScale *= 0.2F;
		this.particleMaxAge = (int) (80.0D / (Math.random() * 0.8D + 0.2D));

		this.motionX *= 0.9D;
		this.motionY *= 0.015D;
		this.motionZ *= 0.9D;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.move(this.motionX, this.motionY, this.motionZ);

		if (this.particleAge == this.particleMaxAge / 2) {
			this.motionX = (origin.x - this.posX) * 0.03;
			this.motionY = (origin.y - this.posY) * 0.03;
			this.motionZ = (origin.z - this.posZ) * 0.03;
		}

		if (this.particleAge < this.particleMaxAge * 0.25) {
			// venture out
			this.motionX *= 0.92 + 0.3D * rand.nextFloat();
			this.motionY = (this.motionY + 0.3 * (-0.5 + rand.nextFloat())) / 2;
			this.motionZ *= 0.92 + 0.3D * rand.nextFloat();
		} else if (this.particleAge < this.particleMaxAge * 0.5) {
			// slow down
			this.motionX *= 0.75 + 0.3D * rand.nextFloat();
			this.motionY = (this.motionY + 0.3 * (-0.5 + rand.nextFloat())) / 2;
			this.motionZ *= 0.75 + 0.3D * rand.nextFloat();
		} else if (this.particleAge < this.particleMaxAge * 0.75) {
			// venture back
			this.motionX *= 0.95;
			this.motionY = (origin.y - this.posY) * 0.03;
			this.motionY = (this.motionY + 0.2 * (-0.5 + rand.nextFloat())) / 2;
			this.motionZ *= 0.95;
		} else {
			// get to origin
			this.motionX = (origin.x - this.posX) * 0.03;
			this.motionY = (origin.y - this.posY) * 0.03;
			this.motionY = (this.motionY + 0.2 * (-0.5 + rand.nextFloat())) / 2;
			this.motionZ = (origin.z - this.posZ) * 0.03;
		}

		if (this.particleAge++ >= this.particleMaxAge) {
			this.setExpired();
		}
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		float minU = 0;
		float maxU = 1;
		float minV = 0;
		float maxV = 1;

		if (this.particleTexture != null) {
			minU = particleTexture.getMinU();
			maxU = particleTexture.getMaxU();
			minV = particleTexture.getMinV();
			maxV = particleTexture.getMaxV();
		}

		float f10 = 0.1F * particleScale;
		float f11 = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
		float f12 = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
		float f13 = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);

		int i = this.getBrightnessForRender(partialTicks);
		int j = i >> 16 & 65535;
		int k = i & 65535;
		buffer.pos(f11 - rotationX * f10 - rotationXY * f10, f12 - rotationZ * f10, f13 - rotationYZ * f10 - rotationXZ * f10).tex(maxU, maxV).color(particleRed, particleGreen, particleBlue, 1.0F).lightmap(j, k).endVertex();
		buffer.pos(f11 - rotationX * f10 + rotationXY * f10, f12 + rotationZ * f10, f13 - rotationYZ * f10 + rotationXZ * f10).tex(maxU, minV).color(particleRed, particleGreen, particleBlue, 1.0F).lightmap(j, k).endVertex();
		buffer.pos(f11 + rotationX * f10 + rotationXY * f10, f12 + rotationZ * f10, f13 + rotationYZ * f10 + rotationXZ * f10).tex(minU, minV).color(particleRed, particleGreen, particleBlue, 1.0F).lightmap(j, k).endVertex();
		buffer.pos(f11 + rotationX * f10 - rotationXY * f10, f12 - rotationZ * f10, f13 + rotationYZ * f10 - rotationXZ * f10).tex(minU, maxV).color(particleRed, particleGreen, particleBlue, 1.0F).lightmap(j, k).endVertex();
	}

	// avoid calculating lighting for bees, it is too much processing
	@Override
	public int getBrightnessForRender(float p_189214_1_) {
		return 15728880;
	}

	// avoid calculating collisions
	@Override
	public void move(double x, double y, double z) {
		this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
		this.resetPositionToBB();
	}

	@Override
	public int getFXLayer() {
		return 1;
	}
}
