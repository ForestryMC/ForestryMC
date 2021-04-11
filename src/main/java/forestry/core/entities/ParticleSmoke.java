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

import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.ParticleTypes;

import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleSmoke extends Particle {
	private final float ignitionParticleScale = 1.0f;    //TODO particles

	public ParticleSmoke(ClientWorld world, double x, double y, double z) {
		super(world, x, y, z, 0, 0, 0);
		this.xd *= 0.8;
		this.yd *= 0.8;
		this.zd *= 0.8;
		this.yd = this.random.nextFloat() * 0.2F + 0.05F;
		this.rCol = this.gCol = this.bCol = 1.0F;
		//TODO - think width and height have to be changed now
		//TODO - or multipleParticleScaleBy()
		//		this.particleScale *= this.rand.nextFloat() / 4;
		//		this.ignitionParticleScale = this.particleScale;
		this.lifetime = (int) (16.0 / (Math.random() * 0.8 + 0.2));
		//		this.setParticleTextureIndex(49);
	}

	@Override
	public int getLightColor(float p_70070_1_) {
		int i = super.getLightColor(p_70070_1_);
		int j = i >> 16 & 255;
		return 240 | j << 16;
	}

	/*@Override
	public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		float f6 = (this.age + partialTicks) / this.maxAge;
		//TODO particles
		//		this.particleScale = this.ignitionParticleScale * (1.0F - f6 * f6);
		//		super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
		//TODO now abstract
	}*/

	@Override
	public void render(IVertexBuilder iVertexBuilder, ActiveRenderInfo activeRenderInfo, float v) {

	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;    //TODO renderType
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
			this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.x, this.y, this.z, this.xd, this.yd, this.zd);
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