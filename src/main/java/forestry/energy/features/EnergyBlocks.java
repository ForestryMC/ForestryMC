package forestry.energy.features;

import forestry.core.config.Constants;
import forestry.core.items.ItemBlockBase;
import forestry.energy.blocks.BlockEngine;
import forestry.energy.blocks.BlockTypeEngine;
import forestry.modules.ForestryModuleUids;
import forestry.modules.features.FeatureBlockGroup;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

public class EnergyBlocks {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(Constants.MOD_ID).getRegistry(ForestryModuleUids.ENERGY);

	public static final FeatureBlockGroup<BlockEngine, BlockTypeEngine> ENGINES = REGISTRY.blockGroup(BlockEngine::new, BlockTypeEngine.VALUES).setIdent("engine").setItem(ItemBlockBase::new).create();

	private EnergyBlocks() {
	}
}
