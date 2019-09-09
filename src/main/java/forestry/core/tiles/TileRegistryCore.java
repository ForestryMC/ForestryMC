package forestry.core.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.core.ModuleCore;
import forestry.core.blocks.BlockRegistryCore;

public class TileRegistryCore extends TileRegistry {

	public final TileEntityType<TileAnalyzer> analyzer;
	public final TileEntityType<TileEscritoire> escritoire;

	public TileRegistryCore() {
		BlockRegistryCore blocks = ModuleCore.getBlocks();

		analyzer = registerTileEntityType(TileAnalyzer::new, "analyzer", blocks.analyzer);
		escritoire = registerTileEntityType(TileEscritoire::new, "escritoire", blocks.escritoire);
	}

}
