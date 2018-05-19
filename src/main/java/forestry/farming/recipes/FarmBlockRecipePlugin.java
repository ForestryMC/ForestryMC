package forestry.farming.recipes;

import java.util.HashSet;
import java.util.Set;

import forestry.farming.models.EnumFarmBlockTexture;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;

@JEIPlugin
public class FarmBlockRecipePlugin implements IModPlugin {

	@Override
	public void register(IModRegistry registry) {
		IStackHelper helper = registry.getJeiHelpers().getStackHelper();
		Set<FarmBlockRecipeWrapper> recipes = new HashSet<>();
		for (FarmBlockRecipe recipe : FarmBlockRecipe.Factory.RECIPES) {
			for (EnumFarmBlockTexture tex : EnumFarmBlockTexture.values()) {
				recipes.add(new FarmBlockRecipeWrapper(recipe, tex, helper));
			}
		}
		registry.addRecipes(recipes, VanillaRecipeCategoryUid.CRAFTING);
	}

}
