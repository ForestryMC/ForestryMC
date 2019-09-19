package forestry.factory.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.core.tiles.TileRegistry;
import forestry.factory.blocks.BlockTypeFactoryPlain;
import forestry.factory.blocks.BlockTypeFactoryTesr;
import forestry.factory.features.FactoryBlocks;

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
		bottler = registerTileEntityType(TileBottler::new, "bottler", FactoryBlocks.TESR.get(BlockTypeFactoryTesr.BOTTLER).block());
		carpenter = registerTileEntityType(TileCarpenter::new, "carpenter", FactoryBlocks.TESR.get(BlockTypeFactoryTesr.CARPENTER).block());
		centrifuge = registerTileEntityType(TileCentrifuge::new, "centrifuge", FactoryBlocks.TESR.get(BlockTypeFactoryTesr.CENTRIFUGE).block());
		fabricator = registerTileEntityType(TileFabricator::new, "fabricator", FactoryBlocks.PLAIN.get(BlockTypeFactoryPlain.FABRICATOR).block());
		fermenter = registerTileEntityType(TileFermenter::new, "fermenter", FactoryBlocks.TESR.get(BlockTypeFactoryTesr.FERMENTER).block());
		rainmaker = registerTileEntityType(TileMillRainmaker::new, "rainmaker", FactoryBlocks.TESR.get(BlockTypeFactoryTesr.RAINMAKER).block());
		moistener = registerTileEntityType(TileMoistener::new, "moistener", FactoryBlocks.TESR.get(BlockTypeFactoryTesr.MOISTENER).block());
		rainTank = registerTileEntityType(TileRaintank::new, "rain_tank", FactoryBlocks.PLAIN.get(BlockTypeFactoryPlain.RAINTANK).block());
		squeezer = registerTileEntityType(TileSqueezer::new, "squeezer", FactoryBlocks.TESR.get(BlockTypeFactoryTesr.SQUEEZER).block());
		still = registerTileEntityType(TileStill::new, "still", FactoryBlocks.TESR.get(BlockTypeFactoryTesr.STILL).block());
	}
}
