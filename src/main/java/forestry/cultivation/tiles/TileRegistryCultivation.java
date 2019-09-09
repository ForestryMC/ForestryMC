package forestry.cultivation.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.core.tiles.TileRegistry;
import forestry.cultivation.ModuleCultivation;
import forestry.cultivation.blocks.BlockRegistryCultivation;

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
		BlockRegistryCultivation blocks = ModuleCultivation.getBlocks();

		ARBORETUM = registerTileEntityType(TileArboretum::new, "arboretum", blocks.arboretum);
		BOG = registerTileEntityType(TileBog::new, "bog", blocks.peatBog);
		CROPS = registerTileEntityType(TileFarmCrops::new, "crops", blocks.farmCrops);
		ENDER = registerTileEntityType(TileFarmEnder::new, "ender", blocks.farmEnder);
		GOURD = registerTileEntityType(TileFarmGourd::new, "gourd", blocks.farmGourd);
		MUSHROOM = registerTileEntityType(TileFarmMushroom::new, "mushroom", blocks.farmMushroom);
		NETHER = registerTileEntityType(TileFarmNether::new, "nether", blocks.farmNether);
	}


}
