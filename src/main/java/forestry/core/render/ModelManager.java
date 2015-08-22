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
import forestry.api.core.IMeshDefinitionObject;
import forestry.api.core.IModelManager;
import forestry.api.core.IModelObject;
import forestry.api.core.IVariantObject;
import forestry.api.core.IModelObject.ModelType;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
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
	public void registerModel(Item item, int meta, String identifier)
	{
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta,  getModelLocation(identifier));
	}
	
	@Override
	public void registerModel(Item item, ItemMeshDefinition definition)
	{
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, definition);
	}
	
	@Override
	public void registerVariant(Item item, String... names)
	{
		ModelBakery.addVariantName(item, names);
	}

	@Override
	public ModelResourceLocation getModelLocation(String identifier) {
		return new ModelResourceLocation("forestry:texture/items/" + identifier, "inventory");
	}
	
	public void registerItemBlockModel(Block block)
	{
		if(((IModelObject) block).getModelType() == ModelType.META)
		{
				if(block instanceof IVariantObject)
					for(int i = 0;i < ((IVariantObject)block).getVariants().length;i++)
					{
						String name = ((IVariantObject)block).getVariants()[i];
						registerModel(Item.getItemFromBlock(block), i, name);
					}
		}
		else if(((IModelObject) block).getModelType() == ModelType.MESHDEFINITION && block instanceof IMeshDefinitionObject)
		{
			registerModel(Item.getItemFromBlock(block), ((IMeshDefinitionObject)block).getMeshDefinition());
		}
		else
		{
			registerModel(Item.getItemFromBlock(block), 0, Item.getItemFromBlock(block).getUnlocalizedName());
		}
		
		if(block instanceof IVariantObject)
			ModelBakery.addVariantName(Item.getItemFromBlock(block), ((IVariantObject)block).getVariants());
	}
	
	public void registerItemModel(Item item)
	{
		if(DimensionManager.getWorld(0).isRemote && item instanceof IModelObject)
		{
			if(((IModelObject)item).getModelType() == ModelType.META)
			{
				if(item instanceof IVariantObject)
					for(int i = 0;i < ((IVariantObject)item).getVariants().length;i++)
					{
						String name = ((IVariantObject)item).getVariants()[i];
						registerModel(item, i, name);
					}
			}
			else if(((IModelObject)item).getModelType() == ModelType.MESHDEFINITION && item instanceof IMeshDefinitionObject)
			{
				registerModel(item, ((IMeshDefinitionObject)item).getMeshDefinition());
			}
			else
			{
				registerModel(item, 0, item.getUnlocalizedName());
			}
		}
		
		if(item instanceof IVariantObject)
			ModelBakery.addVariantName(item, ((IVariantObject)item).getVariants());
	}
	
}
