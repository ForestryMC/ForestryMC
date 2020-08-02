package forestry.arboriculture.features;

import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.tiles.TileArboristChest;
import forestry.arboriculture.tiles.TileFruitPod;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.arboriculture.tiles.TileSapling;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.FeatureTileType;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class ArboricultureTiles {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleArboriculture.class);

    public static final FeatureTileType<TileSapling> SAPLING = REGISTRY.tile(TileSapling::new, "sapling", ArboricultureBlocks.SAPLING_GE::collect);
    public static final FeatureTileType<TileLeaves> LEAVES = REGISTRY.tile(TileLeaves::new, "leaves", ArboricultureBlocks.LEAVES::collect);
    public static final FeatureTileType<TileFruitPod> PODS = REGISTRY.tile(TileFruitPod::new, "pods", ArboricultureBlocks.PODS::getBlocks);
    public static final FeatureTileType<TileArboristChest> ARBORIST_CHEST = REGISTRY.tile(TileArboristChest::new, "arb_chest", ArboricultureBlocks.TREE_CHEST::collect);
}
