package forestry.climatology.features;

import forestry.climatology.ModuleClimatology;
import forestry.climatology.tiles.TileHabitatFormer;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.FeatureTileType;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class ClimatologyTiles {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleClimatology.class);

    public static final FeatureTileType<TileHabitatFormer> HABITAT_FORMER = REGISTRY.tile(TileHabitatFormer::new, "habitat_former", ClimatologyBlocks.HABITATFORMER::collect);

}
