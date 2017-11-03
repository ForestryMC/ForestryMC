package forestry.database.blocks;

import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;

public class BlockRegistryDatabase extends BlockRegistry {

	public final BlockDatabase database;

	public BlockRegistryDatabase() {
		database = new BlockDatabase(BlockTypeDatabase.DATABASE);
		registerBlock(database, new ItemBlockForestry<>(database), "database");
	}
}
