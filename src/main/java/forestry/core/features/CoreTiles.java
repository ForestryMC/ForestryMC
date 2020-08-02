package forestry.core.features;

import forestry.core.ModuleCore;
import forestry.core.blocks.BlockTypeCoreTesr;
import forestry.core.tiles.TileAnalyzer;
import forestry.core.tiles.TileEscritoire;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.FeatureTileType;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class CoreTiles {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleCore.class);

    public static final FeatureTileType<TileAnalyzer> ANALYZER = REGISTRY.tile(TileAnalyzer::new, "analyzer", CoreBlocks.BASE.get(BlockTypeCoreTesr.ANALYZER)::collect);
    public static final FeatureTileType<TileEscritoire> ESCRITOIRE = REGISTRY.tile(TileEscritoire::new, "escritoire", CoreBlocks.BASE.get(BlockTypeCoreTesr.ESCRITOIRE)::collect);

}
