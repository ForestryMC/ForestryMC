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
package forestry.arboriculture;

import com.google.common.base.Preconditions;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IWoodAccess;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.modules.features.FeatureBlockGroup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import javax.annotation.Nullable;
import java.util.*;

public class WoodAccess implements IWoodAccess {
    @Nullable
    private static WoodAccess INSTANCE;

    public static WoodAccess getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WoodAccess();
        }
        return INSTANCE;
    }

    private final Map<WoodBlockKind, WoodMap> woodMaps = new EnumMap<>(WoodBlockKind.class);
    private final List<IWoodType> registeredWoodTypes = new ArrayList<>();

    private WoodAccess() {
        for (WoodBlockKind woodBlockKind : WoodBlockKind.values()) {
            woodMaps.put(woodBlockKind, new WoodMap(woodBlockKind));
        }
        registerVanilla();
    }

    public <T extends Block & IWoodTyped> void registerFeatures(
            FeatureBlockGroup<? extends T, ? extends IWoodType> featureGroup,
            WoodBlockKind kind
    ) {
        for (T block : featureGroup.getBlocks()) {
            registerWithoutVariants(block, kind);
        }
    }

    private void registerVanilla() {
        register(
                EnumVanillaWoodType.OAK,
                WoodBlockKind.LOG,
                false,
                Blocks.OAK_LOG.getDefaultState(),
                new ItemStack(Blocks.OAK_LOG)
        );
        register(
                EnumVanillaWoodType.SPRUCE,
                WoodBlockKind.LOG,
                false,
                Blocks.SPRUCE_LOG.getDefaultState(),
                new ItemStack(Blocks.SPRUCE_LOG)
        );
        register(
                EnumVanillaWoodType.BIRCH,
                WoodBlockKind.LOG,
                false,
                Blocks.BIRCH_LOG.getDefaultState(),
                new ItemStack(Blocks.BIRCH_LOG)
        );
        register(
                EnumVanillaWoodType.JUNGLE,
                WoodBlockKind.LOG,
                false,
                Blocks.JUNGLE_LOG.getDefaultState(),
                new ItemStack(Blocks.JUNGLE_LOG)
        );
        register(
                EnumVanillaWoodType.ACACIA,
                WoodBlockKind.LOG,
                false,
                Blocks.ACACIA_LOG.getDefaultState(),
                new ItemStack(Blocks.ACACIA_LOG)
        );
        register(
                EnumVanillaWoodType.DARK_OAK,
                WoodBlockKind.LOG,
                false,
                Blocks.DARK_OAK_LOG.getDefaultState(),
                new ItemStack(Blocks.DARK_OAK_LOG)
        );

        register(
                EnumVanillaWoodType.OAK,
                WoodBlockKind.PLANKS,
                false,
                Blocks.OAK_PLANKS.getDefaultState(),
                new ItemStack(Blocks.OAK_PLANKS)
        );
        register(
                EnumVanillaWoodType.SPRUCE,
                WoodBlockKind.PLANKS,
                false,
                Blocks.SPRUCE_PLANKS.getDefaultState(),
                new ItemStack(Blocks.SPRUCE_PLANKS)
        );
        register(
                EnumVanillaWoodType.BIRCH,
                WoodBlockKind.PLANKS,
                false,
                Blocks.BIRCH_PLANKS.getDefaultState(),
                new ItemStack(Blocks.BIRCH_PLANKS)
        );
        register(
                EnumVanillaWoodType.JUNGLE,
                WoodBlockKind.PLANKS,
                false,
                Blocks.JUNGLE_PLANKS.getDefaultState(),
                new ItemStack(Blocks.JUNGLE_PLANKS)
        );
        register(
                EnumVanillaWoodType.ACACIA,
                WoodBlockKind.PLANKS,
                false,
                Blocks.ACACIA_PLANKS.getDefaultState(),
                new ItemStack(Blocks.ACACIA_PLANKS)
        );
        register(
                EnumVanillaWoodType.DARK_OAK,
                WoodBlockKind.PLANKS,
                false,
                Blocks.DARK_OAK_PLANKS.getDefaultState(),
                new ItemStack(Blocks.DARK_OAK_PLANKS)
        );

        register(
                EnumVanillaWoodType.OAK,
                WoodBlockKind.SLAB,
                false,
                Blocks.OAK_SLAB.getDefaultState(),
                new ItemStack(Blocks.OAK_SLAB)
        );
        register(
                EnumVanillaWoodType.SPRUCE,
                WoodBlockKind.SLAB,
                false,
                Blocks.SPRUCE_SLAB.getDefaultState(),
                new ItemStack(Blocks.SPRUCE_SLAB)
        );
        register(
                EnumVanillaWoodType.BIRCH,
                WoodBlockKind.SLAB,
                false,
                Blocks.BIRCH_SLAB.getDefaultState(),
                new ItemStack(Blocks.BIRCH_SLAB)
        );
        register(
                EnumVanillaWoodType.JUNGLE,
                WoodBlockKind.SLAB,
                false,
                Blocks.JUNGLE_SLAB.getDefaultState(),
                new ItemStack(Blocks.JUNGLE_SLAB)
        );
        register(
                EnumVanillaWoodType.ACACIA,
                WoodBlockKind.SLAB,
                false,
                Blocks.ACACIA_SLAB.getDefaultState(),
                new ItemStack(Blocks.ACACIA_SLAB)
        );
        register(
                EnumVanillaWoodType.DARK_OAK,
                WoodBlockKind.SLAB,
                false,
                Blocks.DARK_OAK_SLAB.getDefaultState(),
                new ItemStack(Blocks.DARK_OAK_SLAB)
        );

        register(
                EnumVanillaWoodType.OAK,
                WoodBlockKind.LOG,
                false,
                Blocks.OAK_LOG.getDefaultState(),
                new ItemStack(Blocks.OAK_LOG)
        );
        register(
                EnumVanillaWoodType.SPRUCE,
                WoodBlockKind.LOG,
                false,
                Blocks.SPRUCE_LOG.getDefaultState(),
                new ItemStack(Blocks.SPRUCE_LOG)
        );
        register(
                EnumVanillaWoodType.BIRCH,
                WoodBlockKind.LOG,
                false,
                Blocks.BIRCH_LOG.getDefaultState(),
                new ItemStack(Blocks.BIRCH_LOG)
        );
        register(
                EnumVanillaWoodType.JUNGLE,
                WoodBlockKind.LOG,
                false,
                Blocks.JUNGLE_LOG.getDefaultState(),
                new ItemStack(Blocks.JUNGLE_LOG)
        );
        register(
                EnumVanillaWoodType.ACACIA,
                WoodBlockKind.LOG,
                false,
                Blocks.ACACIA_LOG.getDefaultState(),
                new ItemStack(Blocks.ACACIA_LOG)
        );
        register(
                EnumVanillaWoodType.DARK_OAK,
                WoodBlockKind.LOG,
                false,
                Blocks.DARK_OAK_LOG.getDefaultState(),
                new ItemStack(Blocks.DARK_OAK_LOG)
        );

        register(
                EnumVanillaWoodType.OAK,
                WoodBlockKind.FENCE,
                false,
                Blocks.OAK_FENCE.getDefaultState(),
                new ItemStack(Blocks.OAK_FENCE)
        );
        register(
                EnumVanillaWoodType.SPRUCE,
                WoodBlockKind.FENCE,
                false,
                Blocks.SPRUCE_FENCE.getDefaultState(),
                new ItemStack(Blocks.SPRUCE_FENCE)
        );
        register(
                EnumVanillaWoodType.BIRCH,
                WoodBlockKind.FENCE,
                false,
                Blocks.BIRCH_FENCE.getDefaultState(),
                new ItemStack(Blocks.BIRCH_FENCE)
        );
        register(
                EnumVanillaWoodType.JUNGLE,
                WoodBlockKind.FENCE,
                false,
                Blocks.JUNGLE_FENCE.getDefaultState(),
                new ItemStack(Blocks.JUNGLE_FENCE)
        );
        register(
                EnumVanillaWoodType.ACACIA,
                WoodBlockKind.FENCE,
                false,
                Blocks.ACACIA_FENCE.getDefaultState(),
                new ItemStack(Blocks.ACACIA_FENCE)
        );
        register(
                EnumVanillaWoodType.DARK_OAK,
                WoodBlockKind.FENCE,
                false,
                Blocks.DARK_OAK_FENCE.getDefaultState(),
                new ItemStack(Blocks.DARK_OAK_FENCE)
        );

        register(
                EnumVanillaWoodType.OAK,
                WoodBlockKind.FENCE_GATE,
                false,
                Blocks.OAK_FENCE_GATE.getDefaultState(),
                new ItemStack(Blocks.OAK_FENCE_GATE)
        );
        register(
                EnumVanillaWoodType.SPRUCE,
                WoodBlockKind.FENCE_GATE,
                false,
                Blocks.SPRUCE_FENCE_GATE.getDefaultState(),
                new ItemStack(Blocks.SPRUCE_FENCE_GATE)
        );
        register(
                EnumVanillaWoodType.BIRCH,
                WoodBlockKind.FENCE_GATE,
                false,
                Blocks.BIRCH_FENCE_GATE.getDefaultState(),
                new ItemStack(Blocks.BIRCH_FENCE_GATE)
        );
        register(
                EnumVanillaWoodType.JUNGLE,
                WoodBlockKind.FENCE_GATE,
                false,
                Blocks.JUNGLE_FENCE_GATE.getDefaultState(),
                new ItemStack(Blocks.JUNGLE_FENCE_GATE)
        );
        register(
                EnumVanillaWoodType.ACACIA,
                WoodBlockKind.FENCE_GATE,
                false,
                Blocks.ACACIA_FENCE_GATE.getDefaultState(),
                new ItemStack(Blocks.ACACIA_FENCE_GATE)
        );
        register(
                EnumVanillaWoodType.DARK_OAK,
                WoodBlockKind.FENCE_GATE,
                false,
                Blocks.DARK_OAK_FENCE_GATE.getDefaultState(),
                new ItemStack(Blocks.DARK_OAK_FENCE_GATE)
        );

        register(
                EnumVanillaWoodType.OAK,
                WoodBlockKind.STAIRS,
                false,
                Blocks.OAK_STAIRS.getDefaultState(),
                new ItemStack(Blocks.OAK_STAIRS)
        );
        register(
                EnumVanillaWoodType.SPRUCE,
                WoodBlockKind.STAIRS,
                false,
                Blocks.SPRUCE_STAIRS.getDefaultState(),
                new ItemStack(Blocks.SPRUCE_STAIRS)
        );
        register(
                EnumVanillaWoodType.BIRCH,
                WoodBlockKind.STAIRS,
                false,
                Blocks.BIRCH_STAIRS.getDefaultState(),
                new ItemStack(Blocks.BIRCH_STAIRS)
        );
        register(
                EnumVanillaWoodType.JUNGLE,
                WoodBlockKind.STAIRS,
                false,
                Blocks.JUNGLE_STAIRS.getDefaultState(),
                new ItemStack(Blocks.JUNGLE_STAIRS)
        );
        register(
                EnumVanillaWoodType.ACACIA,
                WoodBlockKind.STAIRS,
                false,
                Blocks.ACACIA_STAIRS.getDefaultState(),
                new ItemStack(Blocks.ACACIA_STAIRS)
        );
        register(
                EnumVanillaWoodType.DARK_OAK,
                WoodBlockKind.STAIRS,
                false,
                Blocks.DARK_OAK_STAIRS.getDefaultState(),
                new ItemStack(Blocks.DARK_OAK_STAIRS)
        );

        register(
                EnumVanillaWoodType.OAK,
                WoodBlockKind.DOOR,
                false,
                Blocks.OAK_DOOR.getDefaultState(),
                new ItemStack(Items.OAK_DOOR)
        );
        register(
                EnumVanillaWoodType.SPRUCE,
                WoodBlockKind.DOOR,
                false,
                Blocks.SPRUCE_DOOR.getDefaultState(),
                new ItemStack(Items.SPRUCE_DOOR)
        );
        register(
                EnumVanillaWoodType.BIRCH,
                WoodBlockKind.DOOR,
                false,
                Blocks.BIRCH_DOOR.getDefaultState(),
                new ItemStack(Items.BIRCH_DOOR)
        );
        register(
                EnumVanillaWoodType.JUNGLE,
                WoodBlockKind.DOOR,
                false,
                Blocks.JUNGLE_DOOR.getDefaultState(),
                new ItemStack(Items.JUNGLE_DOOR)
        );
        register(
                EnumVanillaWoodType.ACACIA,
                WoodBlockKind.DOOR,
                false,
                Blocks.ACACIA_DOOR.getDefaultState(),
                new ItemStack(Items.ACACIA_DOOR)
        );
        register(
                EnumVanillaWoodType.DARK_OAK,
                WoodBlockKind.DOOR,
                false,
                Blocks.DARK_OAK_DOOR.getDefaultState(),
                new ItemStack(Items.DARK_OAK_DOOR)
        );
    }

    /**
     * Register wood blocks that have no variant property
     */
    private <T extends Block & IWoodTyped> void registerWithoutVariants(T woodTyped, WoodBlockKind woodBlockKind) {
        boolean fireproof = woodTyped.isFireproof();
        BlockState blockState = woodTyped.getDefaultState();
        IWoodType woodType = woodTyped.getWoodType();
        ItemStack itemStack = new ItemStack(woodTyped);
        if (itemStack.isEmpty()) {
            new ItemStack(woodTyped);
        }
        register(woodType, woodBlockKind, fireproof, blockState, itemStack);
    }

    @Override
    public void register(
            IWoodType woodType,
            WoodBlockKind woodBlockKind,
            boolean fireproof,
            BlockState blockState,
            ItemStack itemStack
    ) {
        if (woodBlockKind == WoodBlockKind.DOOR) {
            fireproof = true;
        }
        Preconditions.checkArgument(!itemStack.isEmpty(), "Empty Itemstack");
        WoodMap woodMap = woodMaps.get(woodBlockKind);
        if (!registeredWoodTypes.contains(woodType)) {
            registeredWoodTypes.add(woodType);
        }
        woodMap.getItem(fireproof).put(woodType, itemStack);
        woodMap.getBlock(fireproof).put(woodType, blockState);
    }

    @Override
    public ItemStack getStack(IWoodType woodType, WoodBlockKind woodBlockKind, boolean fireproof) {
        if (woodBlockKind == WoodBlockKind.DOOR) {
            fireproof = true;
        }
        WoodMap woodMap = woodMaps.get(woodBlockKind);
        ItemStack itemStack = woodMap.getItem(fireproof).get(woodType);
        if (itemStack == null) {
            String errMessage = String.format(
                    "No stack found for %s %s %s",
                    woodType,
                    woodMap.getName(),
                    fireproof ? "fireproof" : "non-fireproof"
            );
            throw new IllegalStateException(errMessage);
        }
        return itemStack.copy();
    }

    @Override
    public BlockState getBlock(IWoodType woodType, WoodBlockKind woodBlockKind, boolean fireproof) {
        if (woodBlockKind == WoodBlockKind.DOOR) {
            fireproof = true;
        }
        WoodMap woodMap = woodMaps.get(woodBlockKind);
        BlockState blockState = woodMap.getBlock(fireproof).get(woodType);
        if (blockState == null) {
            String errMessage = String.format(
                    "No block found for %s %s %s",
                    woodType,
                    woodMap.getName(),
                    fireproof ? "fireproof" : "non-fireproof"
            );
            throw new IllegalStateException(errMessage);
        }
        return blockState;
    }

    @Override
    public List<IWoodType> getRegisteredWoodTypes() {
        return registeredWoodTypes;
    }

    private static class WoodMap {
        private final Map<IWoodType, ItemStack> normalItems = new HashMap<>();
        private final Map<IWoodType, ItemStack> fireproofItems = new HashMap<>();
        private final Map<IWoodType, BlockState> normalBlocks = new HashMap<>();
        private final Map<IWoodType, BlockState> fireproofBlocks = new HashMap<>();
        private final WoodBlockKind woodBlockKind;

        public WoodMap(WoodBlockKind woodBlockKind) {
            this.woodBlockKind = woodBlockKind;
        }

        public String getName() {
            return woodBlockKind.name();
        }

        public Map<IWoodType, ItemStack> getItem(boolean fireproof) {
            return fireproof ? this.fireproofItems : this.normalItems;
        }

        public Map<IWoodType, BlockState> getBlock(boolean fireproof) {
            return fireproof ? this.fireproofBlocks : this.normalBlocks;
        }
    }
}
