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

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.model.IModelState;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.IStateMapperRegister;
import forestry.core.blocks.IColoredBlock;
import forestry.core.config.Constants;
import forestry.core.items.IColoredItem;
import forestry.core.utils.ModelUtil;

@SideOnly(Side.CLIENT)
public class ModelManager implements IModelManager {

	private static final ModelManager instance = new ModelManager();

	/* CUSTOM MODELS*/
	private final List<BlockModelEntry> customBlockModels = new ArrayList<>();
	private final List<ModelEntry> customModels = new ArrayList<>();
	/* ITEM AND BLOCK REGISTERS*/
	private final Set<IItemModelRegister> itemModelRegisters = new HashSet<>();
	private final Set<IStateMapperRegister> stateMapperRegisters = new HashSet<>();
	private final Set<IColoredBlock> blockColorList = new HashSet<>();
	private final Set<IColoredItem> itemColorList = new HashSet<>();
	/* DEFAULT ITEM AND BLOCK MODEL STATES*/
	@Nullable
	private IModelState defaultBlockState;
	@Nullable
	private IModelState defaultItemState;

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
	public ModelResourceLocation getModelLocation(Item item) {
		ResourceLocation resourceLocation = item.getRegistryName();
		Preconditions.checkNotNull(resourceLocation);
		String itemName = resourceLocation.getPath();
		return getModelLocation(itemName);
	}

	@Override
	public ModelResourceLocation getModelLocation(String identifier) {
		return getModelLocation(Constants.MOD_ID, identifier);
	}

	@Override
	public ModelResourceLocation getModelLocation(String modID, String identifier) {
		return new ModelResourceLocation(modID + ":" + identifier, "inventory");
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockClient(Block block) {
		if (block instanceof IItemModelRegister) {
			itemModelRegisters.add((IItemModelRegister) block);
		}
		if (block instanceof IStateMapperRegister) {
			stateMapperRegisters.add((IStateMapperRegister) block);
		}
		if (block instanceof IColoredBlock) {
			blockColorList.add((IColoredBlock) block);
		}
	}

	@SideOnly(Side.CLIENT)
	public void registerItemClient(Item item) {
		if (item instanceof IItemModelRegister) {
			itemModelRegisters.add((IItemModelRegister) item);
		}
		if (item instanceof IColoredItem) {
			itemColorList.add((IColoredItem) item);
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
		for (IColoredBlock blockColor : blockColorList) {
			if (blockColor instanceof Block) {
				blockColors.registerBlockColorHandler(ColoredBlockBlockColor.INSTANCE, (Block) blockColor);
			}
		}

		ItemColors itemColors = minecraft.getItemColors();
		for (IColoredItem itemColor : itemColorList) {
			if (itemColor instanceof Item) {
				itemColors.registerItemColorHandler(ColoredItemItemColor.INSTANCE, (Item) itemColor);
			}
		}
	}

	public IModelState getDefaultBlockState() {
		if (defaultBlockState == null) {
			defaultBlockState = ModelUtil.loadModelState(new ResourceLocation("minecraft:models/block/block"));
		}
		return defaultBlockState;
	}

	public IModelState getDefaultItemState() {
		if (defaultItemState == null) {
			defaultItemState = ModelUtil.loadModelState(new ResourceLocation("minecraft:models/item/generated"));
		}
		return defaultItemState;
	}

	public void registerCustomBlockModel(BlockModelEntry index) {
		customBlockModels.add(index);
		if (index.addStateMapper) {
			StateMapperBase ignoreState = new BlockModeStateMapper(index);
			ModelLoader.setCustomStateMapper(index.block, ignoreState);
		}
	}

	public void registerCustomModel(ModelEntry index) {
		customModels.add(index);
	}

	public void onBakeModels(ModelBakeEvent event) {
		//register custom models
		IRegistry<ModelResourceLocation, IBakedModel> registry = event.getModelRegistry();
		for (final BlockModelEntry entry : customBlockModels) {
			registry.putObject(entry.blockModelLocation, entry.model);
			if (entry.itemModelLocation != null) {
				registry.putObject(entry.itemModelLocation, entry.model);
			}
		}

		for (final ModelEntry entry : customModels) {
			registry.putObject(entry.modelLocation, entry.model);
		}
	}

	@SideOnly(Side.CLIENT)
	private static class ColoredItemItemColor implements IItemColor {
		public static final ColoredItemItemColor INSTANCE = new ColoredItemItemColor();

		private ColoredItemItemColor() {

		}

		@Override
		public int colorMultiplier(ItemStack stack, int tintIndex) {
			Item item = stack.getItem();
			if (item instanceof IColoredItem) {
				return ((IColoredItem) item).getColorFromItemstack(stack, tintIndex);
			}
			return 0xffffff;
		}
	}

	@SideOnly(Side.CLIENT)
	private static class ColoredBlockBlockColor implements IBlockColor {
		public static final ColoredBlockBlockColor INSTANCE = new ColoredBlockBlockColor();

		private ColoredBlockBlockColor() {

		}

		@Override
		public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
			Block block = state.getBlock();
			if (block instanceof IColoredBlock && worldIn != null && pos != null) {
				return ((IColoredBlock) block).colorMultiplier(state, worldIn, pos, tintIndex);
			}
			return 0xffffff;
		}
	}

	private static class BlockModeStateMapper extends StateMapperBase {
		private final BlockModelEntry index;

		public BlockModeStateMapper(BlockModelEntry index) {
			this.index = index;
		}

		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
			return index.blockModelLocation;
		}
	}
}
