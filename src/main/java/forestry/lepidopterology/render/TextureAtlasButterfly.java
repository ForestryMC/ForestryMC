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
package forestry.lepidopterology.render;

import com.google.common.collect.Lists;

import java.awt.image.BufferedImage;
import java.io.IOException;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.data.AnimationMetadataSection;

public class TextureAtlasButterfly extends TextureAtlasSprite {

	public TextureAtlasButterfly(String spriteName) {
		super(spriteName);
	}
	
	@Override
	public void loadSprite(BufferedImage[] images, AnimationMetadataSection meta) throws IOException {
		this.resetSprite();
		int i = images[0].getWidth();
		int j = images[0].getHeight();
		this.width = i;
		this.height = j;
		int[][] aint = new int[images.length][];

		for (int k = 0; k < images.length; ++k) {
			BufferedImage bufferedimage = images[k];

			if (bufferedimage != null) {
				if (k > 0 && (bufferedimage.getWidth() != i >> k || bufferedimage.getHeight() != j >> k)) {
					throw new RuntimeException(String.format("Unable to load miplevel: %d, image is size: %dx%d, expected %dx%d", new Object[]{Integer.valueOf(k), Integer.valueOf(bufferedimage.getWidth()), Integer.valueOf(bufferedimage.getHeight()), Integer.valueOf(i >> k), Integer.valueOf(j >> k)}));
				}

				aint[k] = new int[bufferedimage.getWidth() * bufferedimage.getHeight()];
				bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), aint[k], 0, bufferedimage.getWidth());
			}
		}
		this.framesTextureData.add(aint);
	}
	
	private void resetSprite() {
		this.setFramesTextureData(Lists.<int[][]>newArrayList());
		this.frameCounter = 0;
		this.tickCounter = 0;
	}

	@Override
	public boolean hasAnimationMetadata() {
		return false;
	}

}
