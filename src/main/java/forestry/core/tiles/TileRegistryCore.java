package forestry.core.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.core.blocks.BlockTypeCoreTesr;
import forestry.core.features.CoreBlocks;

public class TileRegistryCore extends TileRegistry {

	public final TileEntityType<TileAnalyzer> analyzer;
	public final TileEntityType<TileEscritoire> escritoire;

	public TileRegistryCore() {
		analyzer = registerTileEntityType(TileAnalyzer::new, "analyzer", CoreBlocks.BASE.get(BlockTypeCoreTesr.ANALYZER).block());
		escritoire = registerTileEntityType(TileEscritoire::new, "escritoire", CoreBlocks.BASE.get(BlockTypeCoreTesr.ESCRITOIRE).block());
	}

}
