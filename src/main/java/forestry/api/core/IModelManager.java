/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IModelManager {
	
	void registerItemModel(Item item, int meta, String modifier, String identifier);
	
	void registerItemModel(Item item, int meta, String identifier);
	
	void registerItemModel(Item item, int meta, boolean withMeta);
	
	ModelResourceLocation getModelLocation(String identifier);
	
	ModelResourceLocation getModelLocation(String modID, String identifier);
	
	ModelResourceLocation getModelLocation(Item item);
	
	ModelResourceLocation getModelLocation(Item item, String identifier);
	
	ModelResourceLocation getModelLocation(Item item, String modifier, String identifier);
	
	ModelResourceLocation getModelLocation(Item item, int meta);
	
	ModelResourceLocation getModelLocation(Item item, int meta, String identifier);
	
	ModelResourceLocation getModelLocation(Item item, int meta, String modifier, String identifier);
	
	void registerItemModel(Item item, ItemMeshDefinition definition);
	
	void registerVariant(Item item, ResourceLocation... resources);
	
	IModelBaker createNewRenderer();
	
}
