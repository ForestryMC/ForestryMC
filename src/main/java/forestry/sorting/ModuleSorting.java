package forestry.sorting;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraftforge.common.capabilities.CapabilityManager;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IFilterLogic;
import forestry.api.modules.ForestryModule;
import forestry.core.capabilities.NullStorage;
import forestry.core.config.Constants;
import forestry.core.network.IPacketRegistry;
import forestry.core.tiles.TileUtil;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.sorting.blocks.BlockRegistrySorting;
import forestry.sorting.network.PacketRegistrySorting;
import forestry.sorting.tiles.TileGeneticFilter;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.SORTING, name = "Sorting", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.sorting.description")
public class ModuleSorting extends BlankForestryModule {
	@Nullable
	private static BlockRegistrySorting blocks;

	public static BlockRegistrySorting getBlocks() {
		Preconditions.checkNotNull(blocks);
		return blocks;
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistrySorting();
	}

	@Override
	public void setupAPI() {
		AlleleManager.filterRegistry = new FilterRegistry();

		CapabilityManager.INSTANCE.register(IFilterLogic.class, new NullStorage<>(), () -> FakeFilterLogic.INSTANCE);
	}

	@Override
	public void disabledSetupAPI() {
		AlleleManager.filterRegistry = new DummyFilterRegistry();

		CapabilityManager.INSTANCE.register(IFilterLogic.class, new NullStorage<>(), () -> FakeFilterLogic.INSTANCE);
	}

	@Override
	public void registerItemsAndBlocks() {
		blocks = new BlockRegistrySorting();
	}

	@Override
	public void preInit() {
		DefaultFilterRuleType.init();
	}

	@Override
	public void doInit() {
		TileUtil.registerTile(TileGeneticFilter.class, "genetic_filter");
		((FilterRegistry) AlleleManager.filterRegistry).init();
	}
}
