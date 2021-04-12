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

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeeExploreParticle extends SpriteTexturedParticle {
	private final Vector3d origin;

	public BeeExploreParticle(ClientWorld world, Vector3d origin, BlockPos destination, int color) {
		super(world, origin.x(), origin.y(), origin.z(), 0.0D, 0.0D, 0.0D);
		this.origin = origin;

		this.xd = (destination.getX() + 0.5 - this.x) * 0.015;
		this.yd = (destination.getY() + 0.5 - this.y) * 0.015;
		this.zd = (destination.getZ() + 0.5 - this.z) * 0.015;

		rCol = (color >> 16 & 255) / 255.0F;
		gCol = (color >> 8 & 255) / 255.0F;
		bCol = (color & 255) / 255.0F;

		this.setSize(0.1F, 0.1F);
		this.quadSize *= 0.2F;
		this.lifetime = (int) (80.0D / (Math.random() * 0.8D + 0.2D));

		this.xd *= 0.9D;
		this.yd *= 0.015D;
		this.zd *= 0.9D;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		this.move(this.xd, this.yd, this.zd);

		if (this.age == this.lifetime / 2) {
			this.xd = (origin.x - this.x) * 0.03;
			this.yd = (origin.y - this.y) * 0.03;
			this.zd = (origin.z - this.z) * 0.03;
		}

		if (this.age < this.lifetime * 0.25) {
			// venture out
			this.xd *= 0.92 + 0.3D * random.nextFloat();
			this.yd = (this.yd + 0.3 * (-0.5 + random.nextFloat())) / 2;
			this.zd *= 0.92 + 0.3D * random.nextFloat();
		} else if (this.age < this.lifetime * 0.5) {
			// slow down
			this.xd *= 0.75 + 0.3D * random.nextFloat();
			this.yd = (this.yd + 0.3 * (-0.5 + random.nextFloat())) / 2;
			this.zd *= 0.75 + 0.3D * random.nextFloat();
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
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	// avoid calculating collisions
	@Override
	public void move(double x, double y, double z) {
		this.setBoundingBox(this.getBoundingBox().move(x, y, z));
		this.setLocationFromBoundingbox();
	}

	// avoid calculating lighting for bees, it is too much processing
	@Override
	public int getLightColor(float partialTick) {
		return 15728880;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<BeeParticleData> {
		private final IAnimatedSprite spriteSet;

		public Factory(IAnimatedSprite sprite) {
			this.spriteSet = sprite;
		}

		@Override
		public Particle createParticle(BeeParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			BeeExploreParticle particle = new BeeExploreParticle(worldIn, typeIn.particleStart, typeIn.direction, typeIn.color);
			particle.pickSprite(spriteSet);
			return particle;
		}
	}
}
