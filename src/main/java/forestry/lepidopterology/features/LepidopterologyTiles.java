package forestry.lepidopterology.features;

import forestry.lepidopterology.ModuleLepidopterology;
import forestry.lepidopterology.tiles.TileCocoon;
import forestry.lepidopterology.tiles.TileLepidopteristChest;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.FeatureTileType;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class LepidopterologyTiles {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleLepidopterology.class);

    public static final FeatureTileType<TileCocoon> SOLID_COCOON = REGISTRY.tile(
            () -> new TileCocoon(true),
            "solid_cocoon",
            LepidopterologyBlocks.COCOON_SOLID::collect
    );
    public static final FeatureTileType<TileCocoon> COCOON = REGISTRY.tile(
            () -> new TileCocoon(false),
            "cocoon",
            LepidopterologyBlocks.COCOON::collect
    );
    public static final FeatureTileType<TileLepidopteristChest> LEPIDOPTERIST_CHEST = REGISTRY.tile(
            TileLepidopteristChest::new,
            "lepidopterologist_chest",
            LepidopterologyBlocks.BUTTERFLY_CHEST::collect
    );
}
