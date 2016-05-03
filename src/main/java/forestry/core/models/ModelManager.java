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
package forestry.core.models;

import javax.annotation.Nonnull;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.IStateMapperRegister;
import forestry.core.config.Constants;
import forestry.core.utils.ItemStackUtil;

@SideOnly(Side.CLIENT)
public class ModelManager implements IModelManager {
	
	private static final ModelManager instance = new ModelManager();
	
	private static final ArrayList<BlockModelIndex> customBlockModels = new ArrayList<>();
	private static final ArrayList<ModelIndex> customModels = new ArrayList<>();

	static {
		ForestryAPI.modelManager = instance;
	}

	public static ModelManager getInstance() {
		return instance;
	}

	@Override
	public void registerItemModel(Item item, int meta, String identifier) {
		ModelLoader.setCustomModelResourceLocation(item, meta, getModelLocation(identifier));
	}

	@Override
	public void registerItemModel(Item item, int meta, String modID, String identifier) {
		ModelLoader.setCustomModelResourceLocation(item, meta, getModelLocation(modID, identifier));
	}

	@Override
	public void registerItemModel(Item item, int meta) {
		ModelLoader.setCustomModelResourceLocation(item, meta, getModelLocation(item));
	}

	@Override
	public void registerItemModel(Item item, ItemMeshDefinition definition) {
		ModelLoader.setCustomMeshDefinition(item, definition);
	}

	@Override
	public void registerVariant(Item item, ResourceLocation... resources) {
		ModelBakery.registerItemVariants(item, resources);
	}

	@Override
	public ModelResourceLocation getModelLocation(Item item) {
		String itemName = ItemStackUtil.getItemNameFromRegistry(item).getResourcePath();
		return getModelLocation(itemName);
	}

	@Override
	public ModelResourceLocation getModelLocation(String identifier) {
		return getModelLocation(Constants.RESOURCE_ID, identifier);
	}

	@Override
	public ModelResourceLocation getModelLocation(String modID, String identifier) {
		return new ModelResourceLocation(modID + ":" + identifier, "inventory");
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels() {
		for (Block block : GameData.getBlockRegistry()) {
			if (block instanceof IItemModelRegister) {
				((IItemModelRegister) block).registerModel(Item.getItemFromBlock(block), getInstance());
			}
			if (block instanceof IStateMapperRegister) {
				((IStateMapperRegister) block).registerStateMapper();
			}
		}
		for (Item item : GameData.getItemRegistry()) {
			if (item instanceof IItemModelRegister) {
				((IItemModelRegister) item).registerModel(item, getInstance());
			}
		}
	}

	public static void registerCustomModels(ModelBakeEvent event) {
		IRegistry<ModelResourceLocation, IBakedModel> registry = event.getModelRegistry();
		for (final BlockModelIndex index : customBlockModels) {
			registry.putObject(index.blockModelLocation, index.model);
			registry.putObject(index.itemModelLocation, index.model);
		}
		
		for (final ModelIndex index : customModels) {
			registry.putObject(index.modelLocation, index.model);
		}
	}
	
	public static void registerCustomBlockModel(@Nonnull BlockModelIndex index) {
		customBlockModels.add(index);
	}
	
	public static void registerCustomModel(@Nonnull ModelIndex index) {
		customModels.add(index);
	}

}
