package forestry.plugins.compat;

import forestry.core.blocks.BlockRegistry;
import forestry.energy.blocks.BlockEngine;
import forestry.energy.blocks.BlockTypeEngine;
import forestry.energy.items.ItemEngine;

public class BlockRegistryIC2 extends BlockRegistry {
	public final BlockEngine electricalEngine;
	public final BlockEngine generator;

	public BlockRegistryIC2() {
		electricalEngine = new BlockEngine(BlockTypeEngine.ELECTRICAL);
		registerBlock(electricalEngine, new ItemEngine(electricalEngine), "electricalEngine");

		generator = new BlockEngine(BlockTypeEngine.GENERATOR);
		registerBlock(generator, new ItemEngine(generator), "generator");
	}
}
