package forestry.core.recipes.json;

import java.util.HashSet;
import java.util.Set;

import forestry.apiculture.items.EnumHoneyComb;
import forestry.arboriculture.charcoal.jei.CombBlockRecipeWrapper;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;

@JEIPlugin
public class CombBlockRecipePlugin implements IModPlugin {

	@Override
	public void register(IModRegistry registry) {
		Set<CombBlockRecipeWrapper> recipes = new HashSet<>();
		for (int i = 0; i < EnumHoneyComb.values().length; i++) {    //TODO - not nice code ATM;
			recipes.add(new CombBlockRecipeWrapper(i));
		}
		registry.addRecipes(recipes, VanillaRecipeCategoryUid.CRAFTING);
	}

}
