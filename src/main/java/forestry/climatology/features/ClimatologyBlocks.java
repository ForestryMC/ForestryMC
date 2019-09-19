package forestry.climatology.features;

import forestry.climatology.blocks.BlockHabitatFormer;
import forestry.core.config.Constants;
import forestry.core.items.ItemBlockForestry;
import forestry.modules.ForestryModuleUids;
import forestry.modules.features.FeatureBlock;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

public class ClimatologyBlocks {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(Constants.MOD_ID).getRegistry(ForestryModuleUids.CLIMATOLOGY);

	public static final FeatureBlock<BlockHabitatFormer, ItemBlockForestry> HABITATFORMER = REGISTRY.block(BlockHabitatFormer::new, ItemBlockForestry::new, "habitat_former");

	private ClimatologyBlocks() {

	}
}
