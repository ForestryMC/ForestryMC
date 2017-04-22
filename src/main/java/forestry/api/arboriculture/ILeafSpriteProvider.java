/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.util.ResourceLocation;

/**
 * Provides icons for leaves. Used by TreeManager.treeFactory.createSpecies()
 * Get a default Forestry implementation from TreeManager.treeFactory.getLeafIconProvider() or implement your own.
 * <p>
 * If you implement your own, be sure to register the icons.
 * Icon registration is not done here because these icons are heavily reused.
 */
public interface ILeafSpriteProvider {
	ResourceLocation getSprite(boolean pollinated, boolean fancy);

	int getColor(boolean pollinated);
}
