package forestry.cultivation;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;
import java.util.Set;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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
		Preconditions.checkState(blocks != null);
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
			'C', coreItems.sturdyCasing,
			'B', coreItems.circuitboards.get(EnumCircuitBoardType.BASIC));

		RecipeUtil.addRecipe("farm_crops", blocks.farmCrops,
			"GTG",
			"TCT",
			"GBG",
			'G', OreDictUtil.BLOCK_GLASS,
			'T', coreItems.tubes.get(EnumElectronTube.BRONZE, 1),
			'C', coreItems.sturdyCasing,
			'B', coreItems.circuitboards.get(EnumCircuitBoardType.BASIC));

		RecipeUtil.addRecipe("peat_bog", blocks.peatBog,
			"GTG",
			"TCT",
			"GBG",
			'G', OreDictUtil.BLOCK_GLASS,
			'T', coreItems.tubes.get(EnumElectronTube.COPPER, 1),
			'C', coreItems.sturdyCasing,
			'B', coreItems.circuitboards.get(EnumCircuitBoardType.BASIC));

		RecipeUtil.addRecipe("farm_mushroom", blocks.farmMushroom,
			"BGR",
			"GAG",
			"RGB",
			'G', OreDictUtil.BLOCK_GLASS,
			'B', Blocks.BROWN_MUSHROOM,
			'R', Blocks.RED_MUSHROOM,
			'A', blocks.arboretum);

		RecipeUtil.addRecipe("farm_gourd", blocks.farmGourd,
			"MGP",
			"GAG",
			"PGM",
			'G', OreDictUtil.BLOCK_GLASS,
			'M', Blocks.MELON_BLOCK,
			'P', Blocks.PUMPKIN,
			'A', blocks.arboretum);

		RecipeUtil.addRecipe("farm_nether", blocks.farmNether,
			"WGW",
			"GAG",
			"WGW",
			'G', OreDictUtil.BLOCK_GLASS,
			'W', Items.NETHER_WART,
			'A', blocks.arboretum);

		for(BlockPlanter planter : getBlocks().getPlanters()){
			RecipeUtil.addRecipe(planter.blockType.getName() + "_manual_managed", planter.get(true), "#  ", "   ", "   ", '#', planter.get(false));
			RecipeUtil.addRecipe(planter.blockType.getName() + "_managed_manual", planter.get(false), "#  ", "   ", "   ", '#', planter.get(true));
		}
	}
}
