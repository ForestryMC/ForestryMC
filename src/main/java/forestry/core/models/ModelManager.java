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
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
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
	
	private final List<BlockModelIndex> customBlockModels = new ArrayList<>();
	private final List<ModelIndex> customModels = new ArrayList<>();

	private final List<IItemModelRegister> itemModelRegisters = new ArrayList<>();
	private final List<IStateMapperRegister> stateMapperRegisters = new ArrayList<>();
	private final List<IBlockColor> blockColorList = new ArrayList<>();
	private final List<IItemColor> itemColorList = new ArrayList<>();

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

	public void registerBlock(Block block) {
		if (block instanceof IItemModelRegister) {
			itemModelRegisters.add((IItemModelRegister) block);
		}
		if (block instanceof IStateMapperRegister) {
			stateMapperRegisters.add((IStateMapperRegister) block);
		}
		if (block instanceof IBlockColor) {
			blockColorList.add((IBlockColor) block);
		}
	}

	public void registerItem(Item item) {
		if (item instanceof IItemModelRegister) {
			itemModelRegisters.add((IItemModelRegister) item);
		}
		if (item instanceof IItemColor) {
			itemColorList.add((IItemColor) item);
		}
	}

	@SideOnly(Side.CLIENT)
	public void registerModels() {
		for (IItemModelRegister itemModelRegister : itemModelRegisters) {
			Item item = null;
			if (itemModelRegister instanceof Block) {
				item = Item.getItemFromBlock((Block) itemModelRegister);
			} else if (itemModelRegister instanceof Item) {
				item = (Item) itemModelRegister;
			}

			if (item != null) {
				itemModelRegister.registerModel(item, this);
			}
		}

		for (IStateMapperRegister stateMapperRegister : stateMapperRegisters) {
			stateMapperRegister.registerStateMapper();
		}
	}

	@SideOnly(Side.CLIENT)
	public void registerItemAndBlockColors() {
		Minecraft minecraft = Minecraft.getMinecraft();
		
		BlockColors blockColors = minecraft.getBlockColors();
		for (IBlockColor blockColor : blockColorList) {
			if (blockColor instanceof Block) {
				blockColors.registerBlockColorHandler(blockColor, (Block) blockColor);
			}
		}

		ItemColors itemColors = minecraft.getItemColors();
		for (IItemColor itemColor : itemColorList) {
			if (itemColor instanceof Item) {
				itemColors.registerItemColorHandler(itemColor, (Item) itemColor);
			}
		}
	}

	public void registerCustomModels(ModelBakeEvent event) {
		IRegistry<ModelResourceLocation, IBakedModel> registry = event.getModelRegistry();
		for (final BlockModelIndex index : customBlockModels) {
			registry.putObject(index.blockModelLocation, index.model);
			registry.putObject(index.itemModelLocation, index.model);
		}
		
		for (final ModelIndex index : customModels) {
			registry.putObject(index.modelLocation, index.model);
		}
	}
	
	public void registerCustomBlockModel(@Nonnull BlockModelIndex index) {
		customBlockModels.add(index);
	}
	
	public void registerCustomModel(@Nonnull ModelIndex index) {
		customModels.add(index);
	}

}
