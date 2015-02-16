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

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

public enum SpriteSheet {
	BLOCKS,
	ITEMS;

	public ResourceLocation getLocation() {
		return ordinal() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture;
	}
	
	public int getSheetOrdinal() {
		return ordinal();
	}
}
