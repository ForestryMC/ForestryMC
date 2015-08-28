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

import forestry.api.core.ForestryAPI;
import forestry.api.core.IModelManager;
import forestry.api.core.IModelProvider;
import forestry.api.core.IModelRegister;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.utils.StringUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelManager implements IModelManager {

	private static ModelManager instance;

	public static ModelManager getInstance() {
		if (instance == null) {
			instance = new ModelManager();
			ForestryAPI.modleManager = instance;
		}

		return instance;
	}
	
	@Override
	public void registerItemModel(Item item, int meta, String identifier)
	{
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, getModelLocation(item, identifier));
		registerVariant(item, item.getUnlocalizedName(new ItemStack(item, 1, meta)) + identifier);
	}
	
	@Override
	public void registerItemModel(Item item, int meta, String modifier, String identifier)
	{
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, getModelLocation(item, modifier, identifier));
		registerVariant(item, item.getUnlocalizedName(new ItemStack(item, 1, meta)) + identifier);
	}
	

	@Override
	public void registerItemModel(Item item, int meta) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, getModelLocation(item, meta));
		registerVariant(item, item.getUnlocalizedName(new ItemStack(item, 1, meta)));
	}
	
	@Override
	public void registerItemModel(Item item, ItemMeshDefinition definition)
	{
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, definition);
	}
	
	@Override
	public void registerVariant(Item item, String... names)
	{
		ModelBakery.addVariantName(item, names);
	}
	
	public static void registerModels()
	{
		for(ForestryBlock block : ForestryBlock.values())
			if(block.block() != null)
				if(block.block() instanceof IModelRegister)
					((IModelRegister)block.block()).registerModel(block.item(), getInstance());
		
		for(ForestryItem item : ForestryItem.values())
			if(item.item() != null)
				if(item.item() instanceof IModelRegister)
					((IModelRegister)item.item()).registerModel(item.item(), getInstance());
	}
	
	@Override
	public ModelResourceLocation getModelLocation(String identifier) {
		return new ModelResourceLocation("forestry:items/" + identifier, "inventory");
	}

	@Override
	public ModelResourceLocation getModelLocation(Item item, int meta) {
		return new ModelResourceLocation("forestry:items/" + StringUtil.cleanItemName(item) + "." + meta, "inventory");
	}

	@Override
	public ModelResourceLocation getModelLocation(Item item, int meta, String identifier) {
		return new ModelResourceLocation("forestry:items/" + StringUtil.cleanItemName(item) + "." + meta + identifier, "inventory");
	}

	@Override
	public ModelResourceLocation getModelLocation(Item item, int meta, String modifier, String identifier) {
		return new ModelResourceLocation("forestry:items/" + modifier + "/" + StringUtil.cleanItemName(item) + "." + meta + identifier, "inventory");
	}
	
	@Override
	public ModelResourceLocation getModelLocation(Item item) {
		return new ModelResourceLocation("forestry:items/" + StringUtil.cleanItemName(item), "inventory");
	}

	@Override
	public ModelResourceLocation getModelLocation(Item item, String identifier) {
		return new ModelResourceLocation("forestry:items/" + StringUtil.cleanItemName(item) + identifier, "inventory");
	}
	
	@Override
	public ModelResourceLocation getModelLocation(Item item, String modifier, String identifier) {
		return new ModelResourceLocation("forestry:items/" + modifier + "/" + StringUtil.cleanItemName(item) + identifier, "inventory");
	}
	
}
