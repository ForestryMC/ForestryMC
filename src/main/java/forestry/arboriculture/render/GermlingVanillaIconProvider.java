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

import net.minecraft.init.Blocks;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IGermlingIconProvider;
import forestry.api.core.ITextureManager;
import forestry.api.core.sprite.ISprite;
import forestry.core.config.ForestryItem;
import forestry.core.fluids.ITankManager;
import forestry.core.render.TextureManager;

public class GermlingVanillaIconProvider implements IGermlingIconProvider {
	private final int vanillaMap;

	private ISprite icon;

	public GermlingVanillaIconProvider(int vanillaMap) {
		this.vanillaMap = vanillaMap;
	}

	@Override
	public void registerIcons() {
		TextureManager manager = TextureManager.getInstance();
		switch (vanillaMap) {
		case 0:
			icon = manager.registerTex("minecraft", "blocks", "oak_sapling");
			break;
		case 1:
			icon = manager.registerTex("minecraft", "blocks", "spruce_sapling");
			break;
		case 2:
			icon = manager.registerTex("minecraft", "blocks", "birch_sapling");
			break;
		case 3:
			icon = manager.registerTex("minecraft", "blocks", "jungle_sapling");
			break;
		case 4:
			icon = manager.registerTex("minecraft", "blocks", "dark_oak_sapling");
			break;
		case 5:
			icon = manager.registerTex("minecraft", "blocks", "acacia_sapling");
			break;
		}
	}

	@Override
	public ISprite getIcon(EnumGermlingType type, int renderPass) {
		return icon;
	}
}
