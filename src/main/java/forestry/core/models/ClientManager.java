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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import deleteme.RegistryNameFinder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;

import forestry.core.blocks.IColoredBlock;
import forestry.core.items.definitions.IColoredItem;
import forestry.core.utils.ResourceUtil;
import forestry.modules.features.FeatureBlock;
import forestry.modules.features.FeatureGroup;
import forestry.modules.features.FeatureItem;
import forestry.modules.features.FeatureTable;

@OnlyIn(Dist.CLIENT)
public class ClientManager {

	private static final ClientManager instance = new ClientManager();

	/* CUSTOM MODELS*/
	private final List<BlockModelEntry> customBlockModels = new ArrayList<>();
	private final List<ModelEntry> customModels = new ArrayList<>();
	/* ITEM AND BLOCK REGISTERS*/
	private final Set<IColoredBlock> blockColorList = new HashSet<>();
	private final Set<IColoredItem> itemColorList = new HashSet<>();
	/* DEFAULT ITEM AND BLOCK MODEL STATES*/
	@Nullable
	private ModelState defaultBlockState;
	@Nullable
	private ModelState defaultItemState;

	public static ClientManager getInstance() {
		return instance;
	}

	@OnlyIn(Dist.CLIENT)
	public void registerBlockClient(Block block) {
		if (block instanceof IColoredBlock) {
			blockColorList.add((IColoredBlock) block);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void registerItemClient(Item item) {
		if (item instanceof IColoredItem) {
			itemColorList.add((IColoredItem) item);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void registerItemAndBlockColors() {
		Minecraft minecraft = Minecraft.getInstance();

		BlockColors blockColors = minecraft.getBlockColors();
		for (IColoredBlock blockColor : blockColorList) {
			if (blockColor instanceof Block) {
				blockColors.register(ColoredBlockBlockColor.INSTANCE, (Block) blockColor);
			}
		}

		ItemColors itemColors = minecraft.getItemColors();
		for (IColoredItem itemColor : itemColorList) {
			if (itemColor instanceof Item) {
				itemColors.register(ColoredItemItemColor.INSTANCE, (Item) itemColor);
			}
		}
	}

	public ModelState getDefaultBlockState() {
		if (defaultBlockState == null) {
			defaultBlockState = ResourceUtil.loadTransform(new ResourceLocation("block/block"));
		}
		return defaultBlockState;
	}

	public ModelState getDefaultItemState() {
		if (defaultItemState == null) {
			defaultItemState = ResourceUtil.loadTransform(new ResourceLocation("item/generated"));
		}
		return defaultItemState;
	}

	public void registerModel(BakedModel model, Object feature) {
		if (feature instanceof FeatureGroup) {
			FeatureGroup<?, ?, ?> group = (FeatureGroup) feature;
			group.getFeatures().forEach(f -> registerModel(model, f));
		} else if (feature instanceof FeatureTable) {
			FeatureTable<?, ?, ?, ?> group = (FeatureTable) feature;
			group.getFeatures().forEach(f -> registerModel(model, f));
		} else if (feature instanceof FeatureBlock block) {
			registerModel(model, block.block(), block.getItem());
		} else if (feature instanceof FeatureItem item) {
			registerModel(model, item.item());
		}
	}

	public void registerModel(BakedModel model, Block block, @Nullable BlockItem item) {
		registerModel(model, block, item, block.getStateDefinition().getPossibleStates());
	}

	public void registerModel(BakedModel model, Block block, @Nullable BlockItem item, Collection<BlockState> states) {
		customBlockModels.add(new BlockModelEntry(model, block, item, states));
	}

	public void registerModel(BakedModel model, Item item) {
		customModels.add(new ModelEntry(new ModelResourceLocation(RegistryNameFinder.getRegistryName(item), "inventory"), model));
	}

	public void onBakeModels(ModelBakeEvent event) {
		//register custom models
		Map<ResourceLocation, BakedModel> registry = event.getModelRegistry();
		for (final BlockModelEntry entry : customBlockModels) {
			for (BlockState state : entry.states) {
				registry.put(BlockModelShaper.stateToModelLocation(state), entry.model);
			}
			if (entry.item != null) {
				ResourceLocation registryName = RegistryNameFinder.getRegistryName(entry.item);
				if (registryName == null) {
					continue;
				}
				registry.put(new ModelResourceLocation(registryName, "inventory"), entry.model);
			}
		}

		for (final ModelEntry entry : customModels) {
			registry.put(entry.modelLocation, entry.model);
		}
	}

	private static class ColoredItemItemColor implements ItemColor {
		public static final ColoredItemItemColor INSTANCE = new ColoredItemItemColor();

		private ColoredItemItemColor() {

		}

		@Override
		public int getColor(ItemStack stack, int tintIndex) {
			Item item = stack.getItem();
			if (item instanceof IColoredItem) {
				return ((IColoredItem) item).getColorFromItemStack(stack, tintIndex);
			}
			return 0xffffff;
		}
	}

	private static class ColoredBlockBlockColor implements BlockColor {
		public static final ColoredBlockBlockColor INSTANCE = new ColoredBlockBlockColor();

		private ColoredBlockBlockColor() {

		}

		@Override
		public int getColor(BlockState state, @Nullable BlockAndTintGetter worldIn, @Nullable BlockPos pos, int tintIndex) {
			Block block = state.getBlock();
			if (block instanceof IColoredBlock && worldIn != null && pos != null) {
				return ((IColoredBlock) block).colorMultiplier(state, worldIn, pos, tintIndex);
			}
			return 0xffffff;
		}
	}

	private static class BlockModelEntry {

		private final BakedModel model;
		private final Block block;
		private final Collection<BlockState> states;
		@Nullable
		private final BlockItem item;

		private BlockModelEntry(BakedModel model, Block block, @Nullable BlockItem item, Collection<BlockState> states) {
			this.model = model;
			this.block = block;
			this.item = item;
			this.states = states;
		}

	}

	private static class ModelEntry {

		private final ModelResourceLocation modelLocation;
		private final BakedModel model;

		private ModelEntry(ModelResourceLocation modelLocation, BakedModel model) {
			this.modelLocation = modelLocation;
			this.model = model;
		}

	}
}
