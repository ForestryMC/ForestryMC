package forestry.sorting;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.registries.IForgeRegistry;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IFilterLogic;
import forestry.api.modules.ForestryModule;
import forestry.core.capabilities.NullStorage;
import forestry.core.config.Constants;
import forestry.core.network.IPacketRegistry;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.sorting.blocks.BlockRegistrySorting;
import forestry.sorting.gui.GuiGeneticFilter;
import forestry.sorting.gui.SortingContainerTypes;
import forestry.sorting.network.PacketRegistrySorting;
import forestry.sorting.tiles.TileRegistrySorting;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.SORTING, name = "Sorting", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.sorting.description")
public class ModuleSorting extends BlankForestryModule {
	@Nullable
	private static BlockRegistrySorting blocks;
	@Nullable
	private static SortingContainerTypes containerTypes;
	@Nullable
	private static TileRegistrySorting tiles;

	public static BlockRegistrySorting getBlocks() {
		Preconditions.checkNotNull(blocks);
		return blocks;
	}

	public static SortingContainerTypes getContainerTypes() {
		Preconditions.checkNotNull(containerTypes);
		return containerTypes;
	}

	public static TileRegistrySorting getTiles() {
		Preconditions.checkNotNull(tiles);
		return tiles;
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistrySorting();
	}

	@Override
	public void setupAPI() {
		AlleleManager.filterRegistry = new FilterRegistry();
	}

	@Override
	public void disabledSetupAPI() {
		AlleleManager.filterRegistry = new DummyFilterRegistry();
	}

	@Override
	public void registerBlocks() {
		blocks = new BlockRegistrySorting();
	}

	@Override
	public void registerContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		containerTypes = new SortingContainerTypes(registry);
	}

	@Override
	public void registerGuiFactories() {
		ScreenManager.registerFactory(getContainerTypes().GENETIC_FILTER, GuiGeneticFilter::new);
	}

	@Override
	public void registerTiles() {
		tiles = new TileRegistrySorting();
	}

	@Override
	public void preInit() {
		CapabilityManager.INSTANCE.register(IFilterLogic.class, new NullStorage<>(), () -> FakeFilterLogic.INSTANCE);

		DefaultFilterRuleType.init();
	}

	@Override
	public void doInit() {
		((FilterRegistry) AlleleManager.filterRegistry).init();
	}
}
