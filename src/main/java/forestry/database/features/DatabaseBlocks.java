package forestry.database.features;

import forestry.core.config.Constants;
import forestry.core.items.ItemBlockForestry;
import forestry.database.blocks.BlockDatabase;
import forestry.database.blocks.BlockTypeDatabase;
import forestry.modules.ForestryModuleUids;
import forestry.modules.features.FeatureBlock;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

public class DatabaseBlocks {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(Constants.MOD_ID).getRegistry(ForestryModuleUids.DATABASE);

	public static final FeatureBlock<BlockDatabase, ItemBlockForestry> DATABASE = REGISTRY.block(() -> new BlockDatabase(BlockTypeDatabase.DATABASE), ItemBlockForestry::new, "database");

	private DatabaseBlocks() {
	}
}
