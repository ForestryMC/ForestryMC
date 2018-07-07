package forestry.plugins;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.item.ItemStack;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.IFarmRegistry;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.farming.logic.ForestryFarmIdentifier;
import forestry.farming.logic.farmables.FarmableAgingCrop;
import forestry.modules.ForestryModuleUids;

@SuppressWarnings("unused")
@ForestryModule(containerID = ForestryCompatPlugins.ID, moduleID = ForestryModuleUids.ROOTS, name = "Roots", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.roots.description")
public class PluginRoots extends CompatPlugin {
	public PluginRoots() {
		super("Roots", "roots");
	}

	@Override
	public void registerRecipes() {
		ImmutableList<String> crops = ImmutableList.of(
			"moonglow",
			"terra_moss",
			"aubergine"
		);

		ImmutableList<String> seeds = ImmutableList.of(
			"moontinged_seed",
			"terra_moss_spore",
			"aubergine_seeds"
		);

		ImmutableList<String> fruits = ImmutableList.of(
			"moonglow_leaf",
			"terra_moss_ball",
			"aubergine_item"
		);

		IFarmRegistry farmRegistry = ForestryAPI.farmRegistry;
		int seedAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		int juiceAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple") / 25;

		for (int i = 0; i < fruits.size(); i++) {
			ItemStack seed = getItemStack(seeds.get(i));
			Block block = getBlock(crops.get(i));
			ItemStack fruit = getItemStack(fruits.get(i));
			if (seed != null && i != 1) {
				RecipeManagers.squeezerManager.addRecipe(10, seed, Fluids.SEED_OIL.getFluid(seedAmount));
			}
			if (fruit != null && i == 2) {
				RecipeManagers.squeezerManager.addRecipe(10, fruit, Fluids.JUICE.getFluid(juiceAmount));
			}
			if (seed != null && block != null) {
				farmRegistry.registerFarmables(ForestryFarmIdentifier.CROPS, new FarmableAgingCrop(seed, block, fruit, BlockCrops.AGE, 7, 0));
			}
		}
	}
}
