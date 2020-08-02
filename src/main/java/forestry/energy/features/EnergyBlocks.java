package forestry.energy.features;

import forestry.core.items.ItemBlockBase;
import forestry.energy.ModuleEnergy;
import forestry.energy.blocks.BlockEngine;
import forestry.energy.blocks.BlockTypeEngine;
import forestry.modules.features.FeatureBlockGroup;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class EnergyBlocks {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleEnergy.class);

    public static final FeatureBlockGroup<BlockEngine, BlockTypeEngine> ENGINES = REGISTRY.blockGroup(BlockEngine::new, BlockTypeEngine.VALUES).itemWithType(ItemBlockBase::new).identifier("engine").create();

    private EnergyBlocks() {
    }
}
