package forestry.sorting.blocks;

import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;

public class BlockRegistrySorting extends BlockRegistry {

	public final BlockGeneticFilter filter;

	public BlockRegistrySorting() {
		filter = new BlockGeneticFilter();
		registerBlock(filter, new ItemBlockForestry<>(filter), "genetic_filter");
	}
}
