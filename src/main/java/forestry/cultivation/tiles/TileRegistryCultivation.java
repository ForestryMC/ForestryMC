package forestry.cultivation.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.core.tiles.TileRegistry;
import forestry.cultivation.blocks.BlockTypePlanter;
import forestry.cultivation.features.CultivationBlocks;

public class TileRegistryCultivation extends TileRegistry {

	public final TileEntityType<TileArboretum> ARBORETUM;
	public final TileEntityType<TileBog> BOG;
	public final TileEntityType<TileFarmCrops> CROPS;
	public final TileEntityType<TileFarmEnder> ENDER;
	public final TileEntityType<TileFarmGourd> GOURD;
	public final TileEntityType<TileFarmMushroom> MUSHROOM;
	public final TileEntityType<TileFarmNether> NETHER;
	//	public final TileEntityType<TilePlantation> PLANTATION;

	public TileRegistryCultivation() {
		ARBORETUM = registerTileEntityType(TileArboretum::new, "arboretum", CultivationBlocks.PLANTER.get(BlockTypePlanter.ARBORETUM).block());
		BOG = registerTileEntityType(TileBog::new, "bog", CultivationBlocks.PLANTER.get(BlockTypePlanter.PEAT_POG).block());
		CROPS = registerTileEntityType(TileFarmCrops::new, "crops", CultivationBlocks.PLANTER.get(BlockTypePlanter.FARM_CROPS).block());
		ENDER = registerTileEntityType(TileFarmEnder::new, "ender", CultivationBlocks.PLANTER.get(BlockTypePlanter.FARM_ENDER).block());
		GOURD = registerTileEntityType(TileFarmGourd::new, "gourd", CultivationBlocks.PLANTER.get(BlockTypePlanter.FARM_GOURD).block());
		MUSHROOM = registerTileEntityType(TileFarmMushroom::new, "mushroom", CultivationBlocks.PLANTER.get(BlockTypePlanter.FARM_MUSHROOM).block());
		NETHER = registerTileEntityType(TileFarmNether::new, "nether", CultivationBlocks.PLANTER.get(BlockTypePlanter.FARM_NETHER).block());
	}


}
