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
package forestry.arboriculture.render;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IGermlingSpriteProvider;
import forestry.core.render.TextureManager;

public class SpriteProviderGermlingVanilla implements IGermlingSpriteProvider {
	private final int vanillaMap;

	private TextureAtlasSprite sprite;

	public SpriteProviderGermlingVanilla(int vanillaMap) {
		this.vanillaMap = vanillaMap;
	}

	@Override
	public void registerIcons() {
		TextureManager manager = TextureManager.getInstance();
		switch (vanillaMap) {
		case 0:
			sprite = manager.getSprite("minecraft", "blocks", "oak_sapling");
			break;
		case 1:
			sprite = manager.getSprite("minecraft", "blocks", "spruce_sapling");
			break;
		case 2:
			sprite = manager.getSprite("minecraft", "blocks", "birch_sapling");
			break;
		case 3:
			sprite = manager.getSprite("minecraft", "blocks", "jungle_sapling");
			break;
		case 4:
			sprite = manager.getSprite("minecraft", "blocks", "dark_oak_sapling");
			break;
		case 5:
			sprite = manager.getSprite("minecraft", "blocks", "acacia_sapling");
			break;
		}
	}

	@Override
	public TextureAtlasSprite getSprite(EnumGermlingType type, int renderPass) {
		return sprite;
	}
}
