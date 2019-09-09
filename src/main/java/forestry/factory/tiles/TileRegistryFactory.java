package forestry.factory.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.core.tiles.TileRegistry;
import forestry.factory.ModuleFactory;
import forestry.factory.blocks.BlockRegistryFactory;

public class TileRegistryFactory extends TileRegistry {

	public final TileEntityType<TileBottler> bottler;
	public final TileEntityType<TileCarpenter> carpenter;
	public final TileEntityType<TileCentrifuge> centrifuge;
	public final TileEntityType<TileFabricator> fabricator;
	public final TileEntityType<TileFermenter> fermenter;
	public final TileEntityType<TileMillRainmaker> rainmaker;
	public final TileEntityType<TileMoistener> moistener;
	public final TileEntityType<TileRaintank> rainTank;
	public final TileEntityType<TileSqueezer> squeezer;
	public final TileEntityType<TileStill> still;

	public TileRegistryFactory() {
		BlockRegistryFactory blocks = ModuleFactory.getBlocks();

		bottler = registerTileEntityType(TileBottler::new, "bottler", blocks.bottler);
		carpenter = registerTileEntityType(TileCarpenter::new, "carpenter", blocks.carpenter);
		centrifuge = registerTileEntityType(TileCentrifuge::new, "centrifuge", blocks.centrifuge);
		fabricator = registerTileEntityType(TileFabricator::new, "fabricator", blocks.fabricator);
		fermenter = registerTileEntityType(TileFermenter::new, "fermenter", blocks.fermenter);
		rainmaker = registerTileEntityType(TileMillRainmaker::new, "rainmaker", blocks.rainmaker);
		moistener = registerTileEntityType(TileMoistener::new, "moistener", blocks.moistener);
		rainTank = registerTileEntityType(TileRaintank::new, "rain_tank", blocks.raintank);
		squeezer = registerTileEntityType(TileSqueezer::new, "squeezer", blocks.squeezer);
		still = registerTileEntityType(TileStill::new, "still", blocks.still);
	}
}
