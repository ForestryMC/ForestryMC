package forestry.database;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.core.network.IPacketRegistry;
import forestry.database.blocks.BlockRegistryDatabase;
import forestry.database.network.PacketRegistryDatabase;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.DATABASE, name = "Database", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.database.description")
public class ModuleDatabase extends BlankForestryModule {
	@Nullable
	private static BlockRegistryDatabase blocks;

	public static BlockRegistryDatabase getBlocks() {
		Preconditions.checkNotNull(blocks);
		return blocks;
	}

	@Override
	public void registerItemsAndBlocks() {
		blocks = new BlockRegistryDatabase();
	}

	@Override
	public void doInit() {
		BlockRegistryDatabase blocks = getBlocks();

		blocks.database.init();
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryDatabase();
	}
}
