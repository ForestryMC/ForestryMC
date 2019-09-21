package forestry.cultivation.features;

import forestry.api.core.IBlockSubtype;
import forestry.core.config.Constants;
import forestry.cultivation.blocks.BlockPlanter;
import forestry.cultivation.blocks.BlockTypePlanter;
import forestry.cultivation.items.ItemBlockPlanter;
import forestry.modules.ForestryModuleUids;
import forestry.modules.features.FeatureBlockGroup;
import forestry.modules.features.FeatureBlockTable;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

public class CultivationBlocks {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(Constants.MOD_ID).getRegistry(ForestryModuleUids.CULTIVATION);

	public static final FeatureBlockTable<BlockPlanter, BlockTypePlanter, BlockPlanter.Mode> PLANTER = REGISTRY.blockTable(BlockPlanter::new, BlockTypePlanter.values(), BlockPlanter.Mode.values()).item(ItemBlockPlanter::new).create();

	private CultivationBlocks() {
	}
}
