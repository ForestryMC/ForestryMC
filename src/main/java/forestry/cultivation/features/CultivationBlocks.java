package forestry.cultivation.features;

import forestry.core.config.Constants;
import forestry.cultivation.blocks.BlockPlanter;
import forestry.cultivation.blocks.BlockTypePlanter;
import forestry.cultivation.items.ItemBlockPlanter;
import forestry.modules.ForestryModuleUids;
import forestry.modules.features.FeatureBlockGroup;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

public class CultivationBlocks {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(Constants.MOD_ID).getRegistry(ForestryModuleUids.CULTIVATION);

	public static final FeatureBlockGroup<BlockPlanter, BlockTypePlanter> PLANTER = REGISTRY.blockGroup(BlockPlanter::new, BlockTypePlanter.values()).setItem(ItemBlockPlanter::new).create();

	private CultivationBlocks() {
	}
}
