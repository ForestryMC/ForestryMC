package forestry.apiculture.features;

import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.blocks.BlockAlvearyType;
import forestry.apiculture.blocks.BlockTypeApiculture;
import forestry.apiculture.multiblock.*;
import forestry.apiculture.tiles.*;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.FeatureTileType;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class ApicultureTiles {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleApiculture.class);

    public static final FeatureTileType<TileHive> HIVE = REGISTRY.tile(TileHive::new, "hive", ApicultureBlocks.BEEHIVE::getBlocks);
    public static final FeatureTileType<TileApiary> APIARY = REGISTRY.tile(TileApiary::new, "apiary", () -> ApicultureBlocks.BASE.get(BlockTypeApiculture.APIARY).collect());
    public static final FeatureTileType<TileBeeHouse> BEE_HOUSE = REGISTRY.tile(TileBeeHouse::new, "bee_house", () -> ApicultureBlocks.BASE.get(BlockTypeApiculture.BEE_HOUSE).collect());
    public static final FeatureTileType<TileCandle> CANDLE = REGISTRY.tile(TileCandle::new, "candle", ApicultureBlocks.CANDLE::collect);
    public static final FeatureTileType<TileApiaristChest> APIARIST_CHEST = REGISTRY.tile(TileApiaristChest::new, "apiarist_chest", ApicultureBlocks.BEE_CHEST::collect);
    public static final FeatureTileType<TileAlvearyPlain> ALVEARY_PLAIN = REGISTRY.tile(TileAlvearyPlain::new, "alveary", () -> ApicultureBlocks.ALVEARY.get(BlockAlvearyType.PLAIN).collect());
    public static final FeatureTileType<TileAlvearySieve> ALVEARY_SIEVE = REGISTRY.tile(TileAlvearySieve::new, "alveary_sieve", () -> ApicultureBlocks.ALVEARY.get(BlockAlvearyType.SIEVE).collect());
    public static final FeatureTileType<TileAlvearySwarmer> ALVEARY_SWARMER = REGISTRY.tile(TileAlvearySwarmer::new, "alveary_swarmer", () -> ApicultureBlocks.ALVEARY.get(BlockAlvearyType.SWARMER).collect());
    public static final FeatureTileType<TileAlvearyHygroregulator> ALVEARY_HYGROREGULATOR = REGISTRY.tile(TileAlvearyHygroregulator::new, "alveary_hygroregulator", () -> ApicultureBlocks.ALVEARY.get(BlockAlvearyType.HYGRO).collect());
    public static final FeatureTileType<TileAlvearyStabiliser> ALVEARY_STABILISER = REGISTRY.tile(TileAlvearyStabiliser::new, "alveary_stabiliser", () -> ApicultureBlocks.ALVEARY.get(BlockAlvearyType.STABILISER).collect());
    public static final FeatureTileType<TileAlvearyFan> ALVEARY_FAN = REGISTRY.tile(TileAlvearyFan::new, "alveary_fan", () -> ApicultureBlocks.ALVEARY.get(BlockAlvearyType.FAN).collect());
    public static final FeatureTileType<TileAlvearyHeater> ALVEARY_HEATER = REGISTRY.tile(TileAlvearyHeater::new, "alveary_heater", () -> ApicultureBlocks.ALVEARY.get(BlockAlvearyType.HEATER).collect());

}
