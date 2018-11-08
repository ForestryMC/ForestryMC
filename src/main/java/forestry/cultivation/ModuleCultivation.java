package forestry.cultivation;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;
import java.util.Set;

import net.minecraft.util.ResourceLocation;

import forestry.api.modules.ForestryModule;
import forestry.core.ModuleCore;
import forestry.core.circuits.EnumCircuitBoardType;
import forestry.core.config.Constants;
import forestry.core.items.EnumElectronTube;
import forestry.core.items.ItemRegistryCore;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.OreDictUtil;
import forestry.cultivation.blocks.BlockPlanter;
import forestry.cultivation.blocks.BlockRegistryCultivation;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.CULTIVATION, name = "Cultivation", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.cultivation.description")
public class ModuleCultivation extends BlankForestryModule {
	@Nullable
	private static BlockRegistryCultivation blocks;

	public static BlockRegistryCultivation getBlocks() {
		Preconditions.checkNotNull(blocks);
		return blocks;
	}

	@Override
	public void registerItemsAndBlocks() {
		blocks = new BlockRegistryCultivation();
	}

	@Override
	public Set<ResourceLocation> getDependencyUids() {
		return ImmutableSet.of(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.CORE),
			new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.FARMING));
	}

	@Override
	public void doInit() {
		BlockRegistryCultivation blocks = getBlocks();

		blocks.arboretum.init();
		blocks.farmCrops.init();
		blocks.farmMushroom.init();
		blocks.farmNether.init();
		blocks.farmGourd.init();
		blocks.farmEnder.init();
		//blocks.plantation.init();
		blocks.peatBog.init();
	}

	@Override
	public void registerRecipes() {
		BlockRegistryCultivation blocks = getBlocks();
		ItemRegistryCore coreItems = ModuleCore.getItems();

		RecipeUtil.addRecipe("arboretum", blocks.arboretum,
			"GTG",
			"TCT",
			"GBG",
			'G', OreDictUtil.BLOCK_GLASS,
			'T', coreItems.tubes.get(EnumElectronTube.GOLD, 1),
			'C', coreItems.flexibleCasing,
			'B', coreItems.circuitboards.get(EnumCircuitBoardType.BASIC));

		RecipeUtil.addRecipe("farm_crops", blocks.farmCrops,
			"GTG",
			"TCT",
			"GBG",
			'G', OreDictUtil.BLOCK_GLASS,
			'T', coreItems.tubes.get(EnumElectronTube.BRONZE, 1),
			'C', coreItems.flexibleCasing,
			'B', coreItems.circuitboards.get(EnumCircuitBoardType.BASIC));

		RecipeUtil.addRecipe("peat_bog", blocks.peatBog,
			"GTG",
			"TCT",
			"GBG",
			'G', OreDictUtil.BLOCK_GLASS,
			'T', coreItems.tubes.get(EnumElectronTube.OBSIDIAN, 1),
			'C', coreItems.flexibleCasing,
			'B', coreItems.circuitboards.get(EnumCircuitBoardType.BASIC));

		RecipeUtil.addRecipe("farm_mushroom", blocks.farmMushroom,
			"GTG",
			"TCT",
			"GBG",
			'G', OreDictUtil.BLOCK_GLASS,
			'T', coreItems.tubes.get(EnumElectronTube.APATITE, 1),
			'C', coreItems.flexibleCasing,
			'B', coreItems.circuitboards.get(EnumCircuitBoardType.BASIC));

		RecipeUtil.addRecipe("farm_gourd", blocks.farmGourd,
			"GTG",
			"TCT",
			"GBG",
			'G', OreDictUtil.BLOCK_GLASS,
			'T', coreItems.tubes.get(EnumElectronTube.LAPIS, 1),
			'C', coreItems.flexibleCasing,
			'B', coreItems.circuitboards.get(EnumCircuitBoardType.BASIC));

		RecipeUtil.addRecipe("farm_nether", blocks.farmNether,
			"GTG",
			"TCT",
			"GBG",
			'G', OreDictUtil.BLOCK_GLASS,
			'T', coreItems.tubes.get(EnumElectronTube.BLAZE, 1),
			'C', coreItems.flexibleCasing,
			'B', coreItems.circuitboards.get(EnumCircuitBoardType.BASIC));

		RecipeUtil.addRecipe("farm_ender", blocks.farmEnder,
			"GTG",
			"TCT",
			"GBG",
			'G', OreDictUtil.BLOCK_GLASS,
			'T', coreItems.tubes.get(EnumElectronTube.ENDER, 1),
			'C', coreItems.flexibleCasing,
			'B', coreItems.circuitboards.get(EnumCircuitBoardType.BASIC));

		for (BlockPlanter planter : getBlocks().getPlanters()) {
			RecipeUtil.addShapelessRecipe(planter.blockType.getName() + "_manual_managed", planter.get(true), planter.get(false));
			RecipeUtil.addShapelessRecipe(planter.blockType.getName() + "_managed_manual", planter.get(false), planter.get(true));
		}
	}
}
