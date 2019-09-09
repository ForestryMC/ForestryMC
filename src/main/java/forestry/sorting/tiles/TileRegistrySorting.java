package forestry.sorting.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.core.tiles.TileRegistry;

public class TileRegistrySorting extends TileRegistry {

	public final TileEntityType<TileGeneticFilter> GENETIC_FILTER;

	public TileRegistrySorting() {
		GENETIC_FILTER = registerTileEntityType(TileGeneticFilter::new, "genetic_filter");
	}
}
