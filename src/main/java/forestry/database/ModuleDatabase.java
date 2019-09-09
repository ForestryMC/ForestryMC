package forestry.database;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;

import net.minecraftforge.registries.IForgeRegistry;

import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.core.network.IPacketRegistry;
import forestry.database.blocks.BlockRegistryDatabase;
import forestry.database.gui.DatabaseContainerTypes;
import forestry.database.gui.GuiDatabase;
import forestry.database.network.PacketRegistryDatabase;
import forestry.database.tiles.TileRegistryDatabase;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.DATABASE, name = "Database", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.database.description")
public class ModuleDatabase extends BlankForestryModule {
	@Nullable
	private static BlockRegistryDatabase blocks;
	@Nullable
	private static DatabaseContainerTypes containerTypes;
	@Nullable
	private static TileRegistryDatabase tiles;

	public static BlockRegistryDatabase getBlocks() {
		Preconditions.checkNotNull(blocks);
		return blocks;
	}

	public static DatabaseContainerTypes getContainerTypes() {
		Preconditions.checkNotNull(containerTypes);
		return containerTypes;
	}

	public static TileRegistryDatabase getTiles() {
		Preconditions.checkNotNull(tiles);
		return tiles;
	}

	@Override
	public void registerContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		containerTypes = new DatabaseContainerTypes(registry);
	}

	@Override
	public void registerGuiFactories() {
		ScreenManager.registerFactory(getContainerTypes().DATABASE, GuiDatabase::new);
	}

	@Override
	public void registerBlocks() {
		blocks = new BlockRegistryDatabase();
	}

	@Override
	public void registerTiles() {
		tiles = new TileRegistryDatabase();
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
