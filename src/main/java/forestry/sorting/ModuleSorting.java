package forestry.sorting;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import net.minecraftforge.common.capabilities.CapabilityManager;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IFilterLogic;
import forestry.api.modules.ForestryModule;
import forestry.apiculture.ModuleApiculture;
import forestry.core.capabilities.NullStorage;
import forestry.core.config.Constants;
import forestry.core.network.IPacketRegistry;
import forestry.core.recipes.RecipeUtil;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.OreDictUtil;
import forestry.lepidopterology.ModuleLepidopterology;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
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
	public void registerRecipes() {
		if (ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			RecipeUtil.addRecipe("genetic_filter_api", new ItemStack(getBlocks().filter, 2),
				"WDW",
				"PGP",
				"BDB",
				'D', OreDictUtil.GEM_DIAMOND,
				'W', OreDictUtil.PLANK_WOOD,
				'G', OreDictUtil.BLOCK_GLASS,
				'B', OreDictUtil.GEAR_BRONZE,
				'P', ModuleApiculture.getItems().propolis);
		}
		if (ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
			RecipeUtil.addRecipe("genetic_filter_arb", new ItemStack(getBlocks().filter, 2),
				"WDW",
				"FGF",
				"BDB",
				'D', OreDictUtil.GEM_DIAMOND,
				'W', OreDictUtil.PLANK_WOOD,
				'G', OreDictUtil.BLOCK_GLASS,
				'B', OreDictUtil.GEAR_BRONZE,
				'F', OreDictUtil.FRUIT_FORESTRY);
		}
		if (ModuleHelper.isEnabled(ForestryModuleUids.LEPIDOPTEROLOGY)) {
			RecipeUtil.addRecipe("genetic_filter_lep", new ItemStack(getBlocks().filter, 2),
				"WDW",
				"FGF",
				"BDB",
				'D', OreDictUtil.GEM_DIAMOND,
				'W', OreDictUtil.PLANK_WOOD,
				'G', OreDictUtil.BLOCK_GLASS,
				'B', OreDictUtil.GEAR_BRONZE,
				'F', ModuleLepidopterology.getItems().caterpillarGE);
		}
	}

	@Override
	public void doInit() {
		TileUtil.registerTile(TileGeneticFilter.class, "genetic_filter");
		((FilterRegistry) AlleleManager.filterRegistry).init();
	}
}
