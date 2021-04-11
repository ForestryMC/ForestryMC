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

import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;

public class ParticleSnow extends SpriteTexturedParticle {
	public static final TextureAtlasSprite[] sprites = new TextureAtlasSprite[3];

	public ParticleSnow(ClientWorld world, double x, double y, double z) {
		super(world, x, y, z, 0.0D, 0.0D, 0.0D);

		this.setSprite(sprites[random.nextInt(sprites.length)]);
		this.quadSize *= 0.5F;
		this.lifetime = (int) (40.0D / (Math.random() * 0.8D + 0.2D));

		this.xd *= 0.01D;
		this.yd *= -0.4D;
		this.zd *= 0.01D;
	}

	/*@Override
	//TODO particles
	public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		double x = this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX;
		double y = this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY;
		double z = this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ;

		float minU = 0;//this.particleTextureIndexX / 16.0F;
		float maxU = minU + 0.0624375F;
		float minV = 0;//this.particleTextureIndexY / 16.0F;
		float maxV = minV + 0.0624375F;
		float scale = 0;//0.1F * this.particleScale;

		//		if (this.particleTexture != null) {
		//			minU = this.particleTexture.getMinU();
		//			maxU = this.particleTexture.getMaxU();
		//			minV = this.particleTexture.getMinV();
		//			maxV = this.particleTexture.getMaxV();
		//		}

		for (int i = 0; i < 5; i++) {
			renderParticle(buffer, x, y, z, rotationX, rotationXZ, rotationZ, rotationYZ, rotationXY, minU, maxU, minV, maxV, scale, partialTicks);
		}
	}*/

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
}
