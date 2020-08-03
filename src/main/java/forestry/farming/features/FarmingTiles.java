package forestry.farming.features;

import forestry.farming.ModuleFarming;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.tiles.*;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.FeatureTileType;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class FarmingTiles {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleFarming.class);

    public static final FeatureTileType<TileFarmControl> CONTROL = REGISTRY.tile(TileFarmControl::new, "control", () -> FarmingBlocks.FARM.getRowBlocks(EnumFarmBlockType.CONTROL));
    public static final FeatureTileType<TileFarmGearbox> GEARBOX = REGISTRY.tile(TileFarmGearbox::new, "gearbox", () -> FarmingBlocks.FARM.getRowBlocks(EnumFarmBlockType.GEARBOX));
    public static final FeatureTileType<TileFarmHatch> HATCH = REGISTRY.tile(TileFarmHatch::new, "hatch", () -> FarmingBlocks.FARM.getRowBlocks(EnumFarmBlockType.HATCH));
    public static final FeatureTileType<TileFarmPlain> PLAIN = REGISTRY.tile(TileFarmPlain::new, "plain", () -> FarmingBlocks.FARM.getRowBlocks(EnumFarmBlockType.PLAIN));
    public static final FeatureTileType<TileFarmValve> VALVE = REGISTRY.tile(TileFarmValve::new, "valve", () -> FarmingBlocks.FARM.getRowBlocks(EnumFarmBlockType.VALVE));
}
