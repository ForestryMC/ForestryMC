package forestry.cultivation.features;

import forestry.cultivation.ModuleCultivation;
import forestry.cultivation.blocks.BlockPlanter;
import forestry.cultivation.blocks.BlockTypePlanter;
import forestry.cultivation.items.ItemBlockPlanter;
import forestry.modules.features.FeatureBlockGroup;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class CultivationBlocks {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleCultivation.class);

	public static final FeatureBlockGroup<BlockPlanter, BlockTypePlanter> PLANTER = REGISTRY.blockGroup(BlockPlanter::new, BlockTypePlanter.values()).item(ItemBlockPlanter::new).create();

	private CultivationBlocks() {
	}
}
