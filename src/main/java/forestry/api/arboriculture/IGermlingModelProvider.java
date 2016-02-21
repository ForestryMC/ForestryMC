/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import forestry.api.core.IModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;

/**
 * Provides icons for saplings, pollen, etc.
 * You must implement this yourself to use TreeManager.treeFactory.createSpecies()
 * There is no default implementation because every sapling has a unique icon.
 */
public interface IGermlingModelProvider {
	void registerModels(IModelManager manager);
	ModelResourceLocation getModel(EnumGermlingType type);
}
