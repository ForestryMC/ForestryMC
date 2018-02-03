/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @deprecated TODO Remove in 1.13: Not needed in the api
 */
@Deprecated
@SideOnly(Side.CLIENT)
public interface IModelManager {

	void registerItemModel(Item item, int meta, String modifier, String identifier);

	void registerItemModel(Item item, int meta, String identifier);

	void registerItemModel(Item item, int meta);

	ModelResourceLocation getModelLocation(Item item);

	ModelResourceLocation getModelLocation(String identifier);

	ModelResourceLocation getModelLocation(String modID, String identifier);

	void registerItemModel(Item item, ItemMeshDefinition definition);

}
