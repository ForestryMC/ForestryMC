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
import forestry.api.arboriculture.IGermlingIconProvider;
import forestry.api.core.sprite.ISprite;
import forestry.core.config.ForestryItem;
import forestry.core.render.TextureManager;

public class GermlingIconProvider implements IGermlingIconProvider {

	private final String name;

	private ISprite icon;

	public GermlingIconProvider(String uid) {
		this.name = uid.substring("forestry.".length());
	}

	@Override
	public void registerIcons() {
		TextureManager manager = TextureManager.getInstance();

		icon = manager.registerTex("blocks", "germlings/sapling." + name);
	}

	@Override
	public ISprite getIcon(EnumGermlingType type, int renderPass) {
		return icon;
	}
}
