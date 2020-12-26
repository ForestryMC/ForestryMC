/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.core.models;

import forestry.core.blocks.IColoredBlock;
import forestry.core.items.IColoredItem;
import forestry.core.utils.ResourceUtil;
import forestry.modules.features.FeatureBlock;
import forestry.modules.features.FeatureGroup;
import forestry.modules.features.FeatureItem;
import forestry.modules.features.FeatureTable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;

import javax.annotation.Nullable;
import java.util.*;

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
    private IModelTransform defaultBlockState;
    @Nullable
    private IModelTransform defaultItemState;

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

    public IModelTransform getDefaultBlockState() {
        if (defaultBlockState == null) {
            defaultBlockState = ResourceUtil.loadTransform(new ResourceLocation("block/block"));
        }
        return defaultBlockState;
    }

    public IModelTransform getDefaultItemState() {
        if (defaultItemState == null) {
            defaultItemState = ResourceUtil.loadTransform(new ResourceLocation("item/generated"));
        }
        return defaultItemState;
    }

    public void registerModel(IBakedModel model, Object feature) {
        if (feature instanceof FeatureGroup) {
            FeatureGroup<?, ?, ?> group = (FeatureGroup) feature;
            group.getFeatures().forEach(f -> registerModel(model, f));
        } else if (feature instanceof FeatureTable) {
            FeatureTable<?, ?, ?, ?> group = (FeatureTable) feature;
            group.getFeatures().forEach(f -> registerModel(model, f));
        } else if (feature instanceof FeatureBlock) {
            FeatureBlock block = (FeatureBlock) feature;
            registerModel(model, block.block(), block.getItem());
        } else if (feature instanceof FeatureItem) {
            FeatureItem item = (FeatureItem) feature;
            registerModel(model, item.item());
        }
    }

    public void registerModel(IBakedModel model, Block block, @Nullable BlockItem item) {
        registerModel(model, block, item, block.getStateContainer().getValidStates());
    }

    public void registerModel(IBakedModel model, Block block, @Nullable BlockItem item, Collection<BlockState> states) {
        customBlockModels.add(new BlockModelEntry(model, block, item, states));
    }

    public void registerModel(IBakedModel model, Item item) {
        customModels.add(new ModelEntry(new ModelResourceLocation(item.getRegistryName(), "inventory"), model));
    }

    public void onBakeModels(ModelBakeEvent event) {
        //register custom models
        Map<ResourceLocation, IBakedModel> registry = event.getModelRegistry();
        for (final BlockModelEntry entry : customBlockModels) {
            for (BlockState state : entry.states) {
                registry.put(BlockModelShapes.getModelLocation(state), entry.model);
            }
            if (entry.item != null) {
                ResourceLocation registryName = entry.item.getRegistryName();
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

    private static class ColoredItemItemColor implements IItemColor {
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

    private static class ColoredBlockBlockColor implements IBlockColor {
        public static final ColoredBlockBlockColor INSTANCE = new ColoredBlockBlockColor();

        private ColoredBlockBlockColor() {

        }

        @Override
        public int getColor(
                BlockState state,
                @Nullable IBlockDisplayReader worldIn,
                @Nullable BlockPos pos,
                int tintIndex
        ) {
            Block block = state.getBlock();
            if (block instanceof IColoredBlock && worldIn != null && pos != null) {
                return ((IColoredBlock) block).colorMultiplier(state, worldIn, pos, tintIndex);
            }
            return 0xffffff;
        }
    }

    private static class BlockModelEntry {
        private final IBakedModel model;
        private final Collection<BlockState> states;
        @Nullable
        private final BlockItem item;

        private BlockModelEntry(
                IBakedModel model,
                Block block,
                @Nullable BlockItem item,
                Collection<BlockState> states
        ) {
            this.model = model;
            this.item = item;
            this.states = states;
        }

    }

    private static class ModelEntry {

        private final ModelResourceLocation modelLocation;
        private final IBakedModel model;

        private ModelEntry(ModelResourceLocation modelLocation, IBakedModel model) {
            this.modelLocation = modelLocation;
            this.model = model;
        }

    }
}
