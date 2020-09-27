package forestry.energy.features;

import forestry.energy.ModuleEnergy;
import forestry.energy.blocks.BlockTypeEngine;
import forestry.energy.tiles.TileEngineBiogas;
import forestry.energy.tiles.TileEngineClockwork;
import forestry.energy.tiles.TileEnginePeat;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.FeatureTileType;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class EnergyTiles {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleEnergy.class);

    public static final FeatureTileType<TileEngineBiogas> BIOGAS_ENGINE = REGISTRY.tile(
            TileEngineBiogas::new,
            "biogas_engine",
            EnergyBlocks.ENGINES.get(BlockTypeEngine.BIOGAS)::collect
    );
    public static final FeatureTileType<TileEngineClockwork> CLOCKWORK_ENGINE = REGISTRY.tile(
            TileEngineClockwork::new,
            "clockwork_engine",
            EnergyBlocks.ENGINES.get(BlockTypeEngine.CLOCKWORK)::collect
    );
    public static final FeatureTileType<TileEnginePeat> PEAT_ENGINE = REGISTRY.tile(
            TileEnginePeat::new,
            "peat_engine",
            EnergyBlocks.ENGINES.get(BlockTypeEngine.PEAT)::collect
    );

    //TODO these need the compat block registry
    //	public final TileEntityType<TileEngine> electricEngine;
    //	public final TileEntityType<TileEuGenerator> generator;
}
