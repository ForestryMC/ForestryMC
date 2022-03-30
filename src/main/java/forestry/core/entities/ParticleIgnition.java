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

import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//TODO particles
@OnlyIn(Dist.CLIENT)
public class ParticleIgnition extends Particle {
	private final float ignitionParticleScale;

	public ParticleIgnition(ClientLevel world, double x, double y, double z) {
		super(world, x, y, z, 0, 0, 0);
		this.xd *= 0.8;
		this.yd *= 0.8;
		this.zd *= 0.8;
		this.yd = this.random.nextFloat() * 0.4F + 0.05F;
		this.rCol = this.gCol = this.bCol = 1.0F;
		//TODO particle stuff
		//		this.particleScale *= this.rand.nextFloat() / 2 + 0.3F;
		this.ignitionParticleScale = 1.0f;//this.particleScale;
		this.lifetime = (int) (16.0 / (Math.random() * 0.8 + 0.2));
		//		this.setParticleTextureIndex(49);
	}

	@Override
	public int getLightColor(float p_70070_1_) {
		int i = super.getLightColor(p_70070_1_);
		short short1 = 240;
		int j = i >> 16 & 255;
		return short1 | j << 16;
	}

	/*@Override
	public void renderParticle(BufferBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		float f6 = (this.age + partialTicks) / this.maxAge;
		//		this.particleScale = this.ignitionParticleScale * (1.0F - f6 * f6);
		//		super.renderParticle(buffer, renderInfo, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
	}*/

	@Override
	public void render(VertexConsumer iVertexBuilder, Camera activeRenderInfo, float v) {

	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if (this.age++ >= this.lifetime) {
			this.remove();
		}

		float f = (float) this.age / (float) this.lifetime;

		if (this.random.nextFloat() > f * 2) {
			this.level.addParticle(ParticleTypes.SMOKE, this.x, this.y, this.z, this.xd, this.yd, this.zd);
		}

		this.yd -= 0.03D;
		this.move(this.xd, this.yd, this.zd);
		this.xd *= 0.999D;
		this.yd *= 0.999D;
		this.zd *= 0.999D;

		if (this.onGround) {
			this.xd *= 0.7;
			this.zd *= 0.7;
		}
	}
}