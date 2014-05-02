/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
