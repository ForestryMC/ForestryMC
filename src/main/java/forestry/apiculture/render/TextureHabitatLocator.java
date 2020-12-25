/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.apiculture.render;

/*@OnlyIn(Dist.CLIENT)
public class TextureHabitatLocator extends TextureAtlasSprite {

	private static TextureHabitatLocator instance;

	public static TextureHabitatLocator getInstance() {
		return instance;
	}

	@Nullable
	private BlockPos targetBiome;
	private boolean targetBiomeFound;

	private double currentAngle;
	private double angleDelta;

	public TextureHabitatLocator(String iconName) {
		//TODO texture size
		super(new ResourceLocation(iconName), 0, 0);
		instance = this;
	}

	public void setTargetCoordinates(@Nullable BlockPos coordinates) {
		this.targetBiome = coordinates;
		this.targetBiomeFound = false;
	}

	@Override
	public void updateAnimation() {
		Minecraft minecraft = Minecraft.getInstance();

		if (minecraft.world != null && minecraft.player != null) {
			BlockPos pos = minecraft.player.getPosition();
			updateCompass(minecraft.world, pos.getX(), pos.getZ(), minecraft.player.rotationYaw);
		} else {
			updateCompass(null, 0.0d, 0.0d, 0.0d);
		}
	}

	private void updateCompass(@Nullable World world, double playerX, double playerZ, double playerYaw) {

		double targetAngle;

		if (world == null || targetBiome == null) {
			// No target has the locator spinning wildly.
			targetAngle = Math.random() * Math.PI * 2.0d;
		} else {
			double xPart = targetBiome.getX() - playerX;
			double zPart = targetBiome.getZ() - playerZ;

			if (Math.abs(xPart) + Math.abs(zPart) < 10 || targetBiomeFound) {
				// spin steadily when the biome is found
				targetAngle = currentAngle + 1;
				targetBiomeFound = true;
			} else {
				playerYaw %= 360.0D;
				targetAngle = -((playerYaw - 90.0f) * Math.PI / 180.0d - Math.atan2(zPart, xPart));
			}
		}

		double angleChange = targetAngle - currentAngle;
		while (angleChange < -Math.PI) {
			angleChange += Math.PI * 2D;
		}

		while (angleChange >= Math.PI) {
			angleChange -= Math.PI * 2D;
		}

		if (angleChange < -1.0D) {
			angleChange = -1.0D;
		}

		if (angleChange > 1.0D) {
			angleChange = 1.0D;
		}

		this.angleDelta += angleChange * 0.1D;
		this.angleDelta *= 0.8D;
		this.currentAngle += this.angleDelta;
		//TODO - check it is frames and not interpolatedframedata
		int i = (int) ((this.currentAngle / (Math.PI * 2D) + 1.0d) * this.frames.length) % this.frames.length;
		while (i < 0) {
			i = (i + this.frames.length) % this.frames.length;
		}

		if (i != this.frameCounter) {
			this.frameCounter = i;
			//TODO - check
			this.frames[this.frameCounter].uploadTextureSub(0, this.x, this.y, false);
		}

	}
}*/
