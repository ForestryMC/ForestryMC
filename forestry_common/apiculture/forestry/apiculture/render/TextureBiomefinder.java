/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class TextureBiomefinder extends TextureAtlasSprite {

	private static TextureBiomefinder instance;
	public static TextureBiomefinder getInstance() { return instance; }

	private ChunkCoordinates targetBiome;

	public double currentAngle;
	public double angleDelta;

	public TextureBiomefinder() {
		super("biomefinder");
		instance = this;
	}

	public void setTargetCoordinates(ChunkCoordinates coordinates) {
		this.targetBiome = coordinates;
	}

	@Override
	public void updateAnimation() {
		Minecraft minecraft = Minecraft.getMinecraft();

		if (minecraft.theWorld != null && minecraft.thePlayer != null)
			updateCompass(minecraft.theWorld, minecraft.thePlayer.posX, minecraft.thePlayer.posZ, minecraft.thePlayer.rotationYaw, false, true);
		else
			updateCompass((World)null, 0.0d, 0.0d, 0.0d, true, true);
	}

	public void updateCompass(World world, double playerX, double playerZ, double playerYaw, boolean par8, boolean hasSpin) {

		double targetAngle = 0.0d;

		if(world == null || targetBiome == null) {
			// No target has the locator spinning wildly.
			targetAngle = Math.random() * Math.PI * 2.0d;
		} else {
			double xPart = targetBiome.posX - playerX;
			double yPart = targetBiome.posZ - playerZ;
			targetAngle = (playerYaw - 90.0f) * Math.PI / 180.0d - Math.atan2(yPart, xPart);
		}

		if(!hasSpin) {
			currentAngle = targetAngle;
		} else {
			double angleChange;

			for (angleChange = targetAngle - currentAngle; angleChange < -Math.PI; angleChange += (Math.PI * 2D))
				;

			while (angleChange >= Math.PI)
				angleChange -= (Math.PI * 2D);

			if (angleChange < -1.0D)
				angleChange = -1.0D;

			if (angleChange > 1.0D)
				angleChange = 1.0D;

			this.angleDelta += angleChange * 0.1D;
			this.angleDelta *= 0.8D;
			this.currentAngle += this.angleDelta;
		}

		int i;

		for (i = (int)((this.currentAngle / (Math.PI * 2D) + 1.0d) * this.framesTextureData.size()) % this.framesTextureData.size(); i < 0; i = (i + this.framesTextureData.size()) % this.framesTextureData.size())
			;

		if (i != this.frameCounter) {
			this.frameCounter = i;
			TextureUtil.uploadTextureMipmap((int[][])this.framesTextureData.get(this.frameCounter), this.width, this.height, this.originX, this.originY, false, false);
		}

	}
}
