package forestry.apiculture.features;

import forestry.api.apiculture.hives.IHiveRegistry;
import forestry.api.core.ItemGroups;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.blocks.*;
import forestry.apiculture.items.EnumHoneyComb;
import forestry.apiculture.items.ItemBlockCandle;
import forestry.apiculture.items.ItemBlockHoneyComb;
import forestry.core.blocks.BlockBase;
import forestry.core.items.ItemBlockBase;
import forestry.core.items.ItemBlockForestry;
import forestry.core.items.ItemBlockWallForestry;
import forestry.modules.features.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraftforge.common.ToolType;

@FeatureProvider
public class ApicultureBlocks {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleApiculture.class);

    public static final FeatureBlockGroup<BlockApiculture, BlockTypeApiculture> BASE = REGISTRY.blockGroup(BlockApiculture::new, BlockTypeApiculture.values()).item((block) -> new ItemBlockForestry<>(block, new Item.Properties().group(ItemGroups.tabApiculture))).create();

    public static final FeatureBlock<BlockBase<BlockTypeApicultureTesr>, ItemBlockBase> BEE_CHEST = REGISTRY.block(() -> new BlockBase<>(BlockTypeApicultureTesr.APIARIST_CHEST, Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).harvestLevel(0)), (block) -> new ItemBlockBase<>(block, new Item.Properties().group(ItemGroups.tabApiculture), BlockTypeApicultureTesr.APIARIST_CHEST), "bee_chest");

    public static final FeatureBlockGroup<BlockBeeHive, IHiveRegistry.HiveType> BEEHIVE = REGISTRY.blockGroup(BlockBeeHive::new, IHiveRegistry.HiveType.VALUES).itemWithType((block, type) -> new ItemBlockForestry<>(block, new Item.Properties().group(type == IHiveRegistry.HiveType.SWARM ? null : ItemGroups.tabApiculture))).identifier("beehive").create();

    public static final FeatureBlock<BlockCandleWall, ItemBlockForestry> CANDLE_WALL = REGISTRY.block(BlockCandleWall::new, "candle_wall");
    public static final FeatureBlock<BlockCandle, ItemBlockForestry> CANDLE = REGISTRY.block(BlockCandle::new, (block) -> new ItemBlockCandle(block, CANDLE_WALL.block()), "candle");
    public static final FeatureBlock<BlockStumpWall, ItemBlockForestry> STUMP_WALL = REGISTRY.block(BlockStumpWall::new, "stump_wall");
    public static final FeatureBlock<BlockStump, ItemBlockWallForestry> STUMP = REGISTRY.block(BlockStump::new, (block) -> new ItemBlockWallForestry<>(block, STUMP_WALL.block(), new Item.Properties().group(ItemGroups.tabApiculture)), "stump");
    public static final FeatureBlockGroup<BlockHoneyComb, EnumHoneyComb> BEE_COMB = REGISTRY.blockGroup(BlockHoneyComb::new, EnumHoneyComb.VALUES).item(ItemBlockHoneyComb::new).identifier("block_bee_comb").create();
    public static final FeatureBlockGroup<BlockAlveary, BlockAlvearyType> ALVEARY = REGISTRY.blockGroup(BlockAlveary::new, BlockAlvearyType.VALUES).item(blockAlveary -> new ItemBlockForestry<>(blockAlveary, new Item.Properties().group(ItemGroups.tabApiculture))).identifier("alveary").create();

    private ApicultureBlocks() {
    }
}
