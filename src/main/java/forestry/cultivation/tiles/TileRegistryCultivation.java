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
		ARBORETUM = registerTileEntityType(TileArboretum::new, "arboretum", CultivationBlocks.PLANTER.getRowBlocks(BlockTypePlanter.ARBORETUM));
		BOG = registerTileEntityType(TileBog::new, "bog", CultivationBlocks.PLANTER.getRowBlocks(BlockTypePlanter.PEAT_POG));
		CROPS = registerTileEntityType(TileFarmCrops::new, "crops", CultivationBlocks.PLANTER.getRowBlocks(BlockTypePlanter.FARM_CROPS));
		ENDER = registerTileEntityType(TileFarmEnder::new, "ender", CultivationBlocks.PLANTER.getRowBlocks(BlockTypePlanter.FARM_ENDER));
		GOURD = registerTileEntityType(TileFarmGourd::new, "gourd", CultivationBlocks.PLANTER.getRowBlocks(BlockTypePlanter.FARM_GOURD));
		MUSHROOM = registerTileEntityType(TileFarmMushroom::new, "mushroom", CultivationBlocks.PLANTER.getRowBlocks(BlockTypePlanter.FARM_MUSHROOM));
		NETHER = registerTileEntityType(TileFarmNether::new, "nether", CultivationBlocks.PLANTER.getRowBlocks(BlockTypePlanter.FARM_NETHER));
	}


}
