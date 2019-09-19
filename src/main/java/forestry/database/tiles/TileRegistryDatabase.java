package forestry.database.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.core.tiles.TileRegistry;
import forestry.database.features.DatabaseBlocks;

public class TileRegistryDatabase extends TileRegistry {

	public final TileEntityType<TileDatabase> DATABASE;

	public TileRegistryDatabase() {
		DATABASE = registerTileEntityType(TileDatabase::new, "database", DatabaseBlocks.DATABASE.block());
	}
}
