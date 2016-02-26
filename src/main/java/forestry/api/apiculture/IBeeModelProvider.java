/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;

import forestry.api.core.IModelManager;

public interface IBeeModelProvider {
	void registerModels(Item item, IModelManager manager);

	ModelResourceLocation getModel(EnumBeeType type);
}
