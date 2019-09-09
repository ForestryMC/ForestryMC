/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @deprecated TODO Remove in 1.13: Not needed in the api, just pass in resourcelocations
 */
@Deprecated
@OnlyIn(Dist.CLIENT)
public interface IModelManager {

	void registerItemModel(Item item, int meta, String modifier, String identifier);

	void registerItemModel(Item item, int meta, String identifier);

	void registerItemModel(Item item, int meta);

	ModelResourceLocation getModelLocation(Item item);

	ModelResourceLocation getModelLocation(String identifier);

	ModelResourceLocation getModelLocation(String modID, String identifier);

	//TODO - itemmeshdefinition doesn't exist any more
	//	void registerItemModel(Item item, ItemMeshDefinition definition);

}
