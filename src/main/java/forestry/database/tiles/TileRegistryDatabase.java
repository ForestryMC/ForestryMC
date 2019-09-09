package forestry.database.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.core.tiles.TileRegistry;
import forestry.database.ModuleDatabase;

public class TileRegistryDatabase extends TileRegistry {

	public final TileEntityType<TileDatabase> DATABASE;

	public TileRegistryDatabase() {
		DATABASE = registerTileEntityType(TileDatabase::new, "database", ModuleDatabase.getBlocks().database);
	}
}
