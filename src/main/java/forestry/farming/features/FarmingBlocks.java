package forestry.farming.features;

import net.minecraft.item.BlockItem;

import forestry.core.config.Constants;
import forestry.farming.blocks.BlockFarm;
import forestry.farming.blocks.BlockMushroom;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.items.ItemBlockFarm;
import forestry.farming.models.EnumFarmMaterial;
import forestry.modules.ForestryModuleUids;
import forestry.modules.features.FeatureBlock;
import forestry.modules.features.FeatureBlockTable;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

public class FarmingBlocks {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(Constants.MOD_ID).getRegistry(ForestryModuleUids.FARMING);

	public static final FeatureBlock<BlockMushroom, BlockItem> MUSHROOM = REGISTRY.block(BlockMushroom::new, "mushroom");
	public static final FeatureBlockTable<BlockFarm, EnumFarmBlockType, EnumFarmMaterial> FARM = REGISTRY.blockTable(BlockFarm::new, EnumFarmBlockType.VALUES, EnumFarmMaterial.values()).item(ItemBlockFarm::new).identifier("farm").create();
}
