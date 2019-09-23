package forestry.apiculture.features;

import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.blocks.BlockTypeApiculture;
import forestry.apiculture.multiblock.TileAlveary;
import forestry.apiculture.tiles.TileApiaristChest;
import forestry.apiculture.tiles.TileApiary;
import forestry.apiculture.tiles.TileBeeHouse;
import forestry.apiculture.tiles.TileCandle;
import forestry.apiculture.tiles.TileHive;
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
	public static final FeatureTileType<TileAlveary> ALVEARY = REGISTRY.tile(TileAlveary::new, "alveary", ApicultureBlocks.ALVEARY::getBlocks);
}
