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
package forestry.apiculture.particles;

import javax.annotation.Nullable;

import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BeeTargetEntityParticle extends TextureSheetParticle {
	private final Vec3 origin;
	@Nullable
	private final Entity entity;

	public BeeTargetEntityParticle(ClientLevel world, double x, double y, double z, @Nullable Entity entity, int color) {
		super(world, x, y, z, 0.0D, 0.0D, 0.0D);
		this.origin = new Vec3(x, y, z);
		this.entity = entity;

		this.xd = (entity.getX() - this.x) * 0.015;
		this.yd = (entity.getY() + 1.62F - this.y) * 0.015;
		this.zd = (entity.getZ() - this.z) * 0.015;

		rCol = (color >> 16 & 255) / 255.0F;
		gCol = (color >> 8 & 255) / 255.0F;
		bCol = (color & 255) / 255.0F;

		this.setSize(0.1F, 0.1F);
		this.quadSize *= 0.2F;
		this.lifetime = (int) (80.0D / (Math.random() * 0.8D + 0.2D));

		this.xd *= 0.9D;
		this.yd *= 0.9D;
		this.zd *= 0.9D;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void tick() {
		if (entity == null) {
			remove();
			return;
		}
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		this.move(this.xd, this.yd, this.zd);

		if (this.age == this.lifetime / 2) {
			this.xd = (origin.x - this.x) * 0.03;
			this.yd = (origin.y - this.y) * 0.03;
			this.zd = (origin.z - this.z) * 0.03;
		}

		if (this.age < this.lifetime * 0.5) {
			// fly near the entity
			this.xd = (entity.getX() - this.x) * 0.09;
			this.xd = (this.xd + 0.2 * (-0.5 + random.nextFloat())) / 2;
			this.yd = (entity.getY() + 1.62F - this.y) * 0.03;
			this.yd = (this.yd + 0.4 * (-0.5 + random.nextFloat())) / 4;
			this.zd = (entity.getZ() - this.z) * 0.09;
			this.zd = (this.zd + 0.2 * (-0.5 + random.nextFloat())) / 2;
		} else if (this.age < this.lifetime * 0.75) {
			// venture back
			this.xd *= 0.95;
			this.yd = (origin.y - this.y) * 0.03;
			this.yd = (this.yd + 0.2 * (-0.5 + random.nextFloat())) / 2;
			this.zd *= 0.95;
		} else {
			// get to origin
			this.xd = (origin.x - this.x) * 0.03;
			this.yd = (origin.y - this.y) * 0.03;
			this.yd = (this.yd + 0.2 * (-0.5 + random.nextFloat())) / 2;
			this.zd = (origin.z - this.z) * 0.03;
		}

		if (this.age++ >= this.lifetime) {
			this.remove();
		}
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	// avoid calculating collisions
	@Override
	public void move(double x, double y, double z) {
		this.setBoundingBox(this.getBoundingBox().move(x, y, z));
		this.setLocationFromBoundingbox();
	}

	// avoid calculating lighting for bees, it is too much processing
	@Override
	public int getLightColor(float p_189214_1_) {
		return 15728880;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements ParticleProvider<BeeTargetParticleData> {
		private final SpriteSet spriteSet;

		public Factory(SpriteSet sprite) {
			this.spriteSet = sprite;
		}

		@Override
		public Particle createParticle(BeeTargetParticleData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			BeeTargetEntityParticle particle = new BeeTargetEntityParticle(worldIn, x, y, z, worldIn.getEntity(typeIn.entity), typeIn.color);
			particle.pickSprite(spriteSet);
			return particle;
		}
	}
}
