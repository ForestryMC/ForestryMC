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
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.ParticleTypes;

import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SmokeParticle extends Particle {
	private final float ignitionParticleScale = 1.0f;

	public SmokeParticle(ClientWorld world, double x, double y, double z) {
		super(world, x, y, z, 0, 0, 0);
		this.xd *= 0.8;
		this.yd *= 0.8;
		this.zd *= 0.8;
		this.yd = this.random.nextFloat() * 0.4F + 0.05F;
		this.rCol = this.gCol = bCol = 1.0F;
		this.lifetime = (int) (16.0 / (Math.random() * 0.8 + 0.2));
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
		} else {
			float f = (float) this.age / (float) lifetime;

			if (this.random.nextFloat() > f * 2) {
				this.level.addParticle(ParticleTypes.LARGE_SMOKE, x, y, z, xd, yd, zd);
			}

			this.move(this.xd, this.yd, this.zd);
			if (this.y == this.yo) {
				this.xd *= 1.1D;
				this.zd *= 1.1D;
			}

			this.xd *= (double) 0.96F;
			this.yd *= (double) 0.96F;
			this.zd *= (double) 0.96F;
			if (this.onGround) {
				this.xd *= (double) 0.7F;
				this.zd *= (double) 0.7F;
			}
		}
	}

	@Override
	public void render(IVertexBuilder iVertexBuilder, ActiveRenderInfo activeRenderInfo, float v) {

	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public int getLightColor(float partialTick) {
		int i = super.getLightColor(partialTick);
		int j = i >> 16 & 255;
		return 240 | j << 16;
	}
}