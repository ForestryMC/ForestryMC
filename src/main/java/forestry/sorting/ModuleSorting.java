package forestry.sorting;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import net.minecraftforge.common.capabilities.CapabilityManager;

import net.minecraftforge.fml.common.registry.GameRegistry;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IFilterLogic;
import forestry.api.modules.ForestryModule;
import forestry.apiculture.ModuleApiculture;
import forestry.core.capabilities.NullStorage;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.network.IPacketRegistry;
import forestry.core.recipes.json.RecipeConverter;
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
		Preconditions.checkState(blocks != null);
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
		if (!Config.resetRecipes) {
			return;
		}
		String id = ForestryModuleUids.SORTING;
		List<Object> ingredients = new ArrayList<>();
		if (ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			ingredients.add(ModuleApiculture.getItems().propolis);
		}
		if (ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
			ingredients.add(OreDictUtil.FRUIT_FORESTRY);
		}
		if (ModuleHelper.isEnabled(ForestryModuleUids.LEPIDOPTEROLOGY)) {
			ingredients.add(ModuleLepidopterology.getItems().caterpillarGE);
		}
		RecipeConverter.addRecipeMultipleIngredients(new ItemStack(getBlocks().filter, 2), id,
				"WDW",
				"FGF",
				"BDB",
				'D', OreDictUtil.GEM_DIAMOND,
				'W', OreDictUtil.PLANK_WOOD,
				'G', OreDictUtil.BLOCK_GLASS,
				'B', OreDictUtil.GEAR_BRONZE,
				'F', ingredients);
	}

	@Override
	public void doInit() {
		GameRegistry.registerTileEntity(TileGeneticFilter.class, "forestry.GeneticFilter");
		((FilterRegistry) AlleleManager.filterRegistry).init();
	}
}
