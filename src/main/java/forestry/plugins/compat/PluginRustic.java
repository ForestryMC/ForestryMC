package forestry.plugins.compat;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.item.ItemStack;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.IFarmRegistry;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.farming.logic.FarmableAgingCrop;
import forestry.farming.logic.FarmableRusticGrape;
import forestry.farming.logic.FarmableRusticSapling;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;

@ForestryPlugin(pluginID = ForestryPluginUids.ROOTS, name = "rustic", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.plugin.rustic.description")
public class PluginRustic extends CompatPlugin {

	public PluginRustic() {
		super("Rustic", "rustic");
	}

	@Override
	public void registerRecipes() {
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING)) {
			ImmutableList<String> fruits = ImmutableList.of(
				"tomato",
				"chili_pepper"
			);
			ImmutableList<String> crops = ImmutableList.of(
				"tomato",
				"chili"
			);
			PropertyInteger cropAge = PropertyInteger.create("age", 0, 3);
			int seedAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
			int juiceAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple");
			IFarmRegistry farmRegistry = ForestryAPI.farmRegistry;

			ItemStack grapes = getItemStack( "grapes");
			ItemStack grapeSeeds = getItemStack( "grape_stem");
			Block grapeLeaves = getBlock("grape_leaves");
			if (grapeSeeds != null) {
				RecipeManagers.squeezerManager.addRecipe(10, grapeSeeds, Fluids.SEED_OIL.getFluid(seedAmount));
			}
			if (grapes != null) {
				RecipeManagers.squeezerManager.addRecipe(10, grapes, Fluids.JUICE.getFluid(juiceAmount / 12));
			}
			if(grapeLeaves != null){
				farmRegistry.registerFarmables("farmOrchard", new FarmableRusticGrape(grapeLeaves));
			}

			ItemStack sapling = getItemStack("sapling");
			ItemStack ironBerries = getItemStack("ironberries");
			ItemStack olives = getItemStack("olives");
			if(sapling != null && ironBerries != null && olives != null){
				farmRegistry.registerFarmables("farmArboreal", new FarmableRusticSapling(sapling.getItem(), new ItemStack[]{ironBerries, olives}));
			}

			for(int i = 0;i <fruits.size();i++){
				String fruitName = fruits.get(i);
				ItemStack seeds = getItemStack( fruitName + "_seeds");
				Block block = getBlock(crops.get(i) + "_crop");
				ItemStack fruit = getItemStack(fruitName);
				if (seeds != null) {
					RecipeManagers.squeezerManager.addRecipe(10, seeds, Fluids.SEED_OIL.getFluid(seedAmount));
				}
				if(fruit != null){
					RecipeManagers.squeezerManager.addRecipe(10, fruit, Fluids.JUICE.getFluid(juiceAmount / 25));
				}
				if (seeds != null && block != null) {
					farmRegistry.registerFarmables("farmOrchard", new FarmableAgingCrop(seeds, block, cropAge, 3, 2));
				}
			}
		}
	}
}
