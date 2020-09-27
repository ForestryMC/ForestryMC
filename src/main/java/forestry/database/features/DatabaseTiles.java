package forestry.database.features;

import forestry.database.ModuleDatabase;
import forestry.database.tiles.TileDatabase;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.FeatureTileType;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class DatabaseTiles {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleDatabase.class);

    public static final FeatureTileType<TileDatabase> DATABASE = REGISTRY.tile(
            TileDatabase::new,
            "database",
            DatabaseBlocks.DATABASE::collect
    );
}
