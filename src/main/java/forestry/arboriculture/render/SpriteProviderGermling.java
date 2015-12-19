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

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IGermlingSpriteProvider;
import forestry.core.render.TextureManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class SpriteProviderGermling implements IGermlingSpriteProvider {

	private final String name;

	private TextureAtlasSprite sprite;

	public SpriteProviderGermling(String uid) {
		this.name = uid.substring("forestry.".length());
	}

	@Override
	public void registerIcons() {
		TextureManager manager = TextureManager.getInstance();

		sprite = TextureManager.registerSprite("blocks/germlings/sapling." + name);
	}

	@Override
	public TextureAtlasSprite getSprite(EnumGermlingType type, int renderPass) {
		return sprite;
	}
}
