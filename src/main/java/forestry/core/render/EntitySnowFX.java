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

import java.util.Random;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntitySnowFX extends EntityFX {

	public static TextureAtlasSprite icons[];
	private static final Random spreadRand = new Random();

	public EntitySnowFX(World world, double x, double y, double z, float motionScaleX, float motionScaleY, float motionScaleZ) {
		super(world, x, y, z, 0.0D, 0.0D, 0.0D);

		this.setParticleIcon(icons[rand.nextInt(icons.length)]);
		this.particleScale *= 0.5F;
		this.particleMaxAge = (int) (40.0D / (Math.random() * 0.8D + 0.2D));
		this.noClip = true;

		this.motionX *= 0.01D;
		this.motionY *= -0.4D;
		this.motionZ *= 0.01D;
	}

	@Override
	public int getFXLayer() {
		return 2;
	}
	
	@Override
	public void renderParticle(WorldRenderer worldRenderer, Entity entity, float timeStep, float rotationX, float rotationXZ, float rotationZ, float rotationYZ, float rotationXY) {
		double x = (this.prevPosX + (this.posX - this.prevPosX) * timeStep - interpPosX);
		double y = (this.prevPosY + (this.posY - this.prevPosY) * timeStep - interpPosY);
		double z = (this.prevPosZ + (this.posZ - this.prevPosZ) * timeStep - interpPosZ);

		float minU = this.particleTextureIndexX / 16.0F;
		float maxU = minU + 0.0624375F;
		float minV = this.particleTextureIndexY / 16.0F;
		float maxV = minV + 0.0624375F;
		float scale = 0.1F * this.particleScale;

		if (this.particleIcon != null) {
			minU = this.particleIcon.getMinU();
			maxU = this.particleIcon.getMaxU();
			minV = this.particleIcon.getMinV();
			maxV = this.particleIcon.getMaxV();
		}

		float brightness = 1.0F;
		worldRenderer.setColorRGBA_F(this.particleRed * brightness, this.particleGreen * brightness, this.particleBlue * brightness, this.particleAlpha);

		spreadRand.setSeed(701);

		for (int i = 0; i < 5; i++) {
			renderParticle(worldRenderer, x + spreadRand.nextGaussian() * 8, y, z + spreadRand.nextGaussian() * 8, rotationX, rotationXZ, rotationZ, rotationYZ, rotationXY, minU, maxU, minV, maxV, scale);
		}
	}

	private void renderParticle(WorldRenderer worldRenderer, double x, double y, double z, float rotationX, float rotationXZ, float rotationZ, float rotationYZ, float rotationXY,
			float minU, float maxU, float minV, float maxV, float scale) {
		worldRenderer.addVertexWithUV((x - rotationX * scale - rotationYZ * scale), (y - rotationXZ * scale), (z - rotationZ * scale - rotationXY * scale), maxU, maxV);
		worldRenderer.addVertexWithUV((x - rotationX * scale + rotationYZ * scale), (y + rotationXZ * scale), (z - rotationZ * scale + rotationXY * scale), maxU, minV);
		worldRenderer.addVertexWithUV((x + rotationX * scale + rotationYZ * scale), (y + rotationXZ * scale), (z + rotationZ * scale + rotationXY * scale), minU, minV);
		worldRenderer.addVertexWithUV((x + rotationX * scale - rotationYZ * scale), (y - rotationXZ * scale), (z + rotationZ * scale - rotationXY * scale), minU, maxV);
	}
}
