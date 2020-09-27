package forestry.database.features;

import forestry.core.items.ItemBlockForestry;
import forestry.database.ModuleDatabase;
import forestry.database.blocks.BlockDatabase;
import forestry.database.blocks.BlockTypeDatabase;
import forestry.modules.features.FeatureBlock;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class DatabaseBlocks {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleDatabase.class);

    public static final FeatureBlock<BlockDatabase, ItemBlockForestry> DATABASE = REGISTRY.block(() -> new BlockDatabase(
            BlockTypeDatabase.DATABASE), ItemBlockForestry::new, "database");

    private DatabaseBlocks() {
    }
}
