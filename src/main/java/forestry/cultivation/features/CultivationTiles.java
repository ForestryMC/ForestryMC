package forestry.cultivation.features;

import forestry.cultivation.ModuleCultivation;
import forestry.cultivation.blocks.BlockTypePlanter;
import forestry.cultivation.tiles.TileArboretum;
import forestry.cultivation.tiles.TileBog;
import forestry.cultivation.tiles.TileFarmCrops;
import forestry.cultivation.tiles.TileFarmEnder;
import forestry.cultivation.tiles.TileFarmGourd;
import forestry.cultivation.tiles.TileFarmMushroom;
import forestry.cultivation.tiles.TileFarmNether;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.FeatureTileType;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class CultivationTiles {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleCultivation.class);

	public static final FeatureTileType<TileArboretum> ARBORETUM = REGISTRY.tile(TileArboretum::new, "arboretum", () -> CultivationBlocks.PLANTER.get(BlockTypePlanter.ARBORETUM).collect());
	public static final FeatureTileType<TileBog> BOG = REGISTRY.tile(TileBog::new, "bog", () -> CultivationBlocks.PLANTER.get(BlockTypePlanter.PEAT_POG).collect());
	public static final FeatureTileType<TileFarmCrops> CROPS = REGISTRY.tile(TileFarmCrops::new, "crops", () -> CultivationBlocks.PLANTER.get(BlockTypePlanter.FARM_CROPS).collect());
	public static final FeatureTileType<TileFarmEnder> ENDER = REGISTRY.tile(TileFarmEnder::new, "ender", () -> CultivationBlocks.PLANTER.get(BlockTypePlanter.FARM_ENDER).collect());
	public static final FeatureTileType<TileFarmGourd> GOURD = REGISTRY.tile(TileFarmGourd::new, "gourd", () -> CultivationBlocks.PLANTER.get(BlockTypePlanter.FARM_GOURD).collect());
	public static final FeatureTileType<TileFarmMushroom> MUSHROOM = REGISTRY.tile(TileFarmMushroom::new, "mushroom", () -> CultivationBlocks.PLANTER.get(BlockTypePlanter.FARM_MUSHROOM).collect());
	public static final FeatureTileType<TileFarmNether> NETHER = REGISTRY.tile(TileFarmNether::new, "nether", () -> CultivationBlocks.PLANTER.get(BlockTypePlanter.FARM_NETHER).collect());
}
