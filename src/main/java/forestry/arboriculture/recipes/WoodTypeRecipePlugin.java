package forestry.arboriculture.recipes;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IWoodType;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;

@JEIPlugin
public class WoodTypeRecipePlugin implements IModPlugin {

	@Override
	public void register(IModRegistry registry) {
		Set<WoodTypeRecipeWrapper> wrappers = new HashSet<>();
		Set<IWoodType> woodTypes = new HashSet<>();
		Collections.addAll(woodTypes, EnumForestryWoodType.values());
		Collections.addAll(woodTypes, EnumVanillaWoodType.values());
		for (WoodTypeRecipeBase recipe : WoodTypeRecipeFactory.RECIPES) {
			for (IWoodType woodType : woodTypes) {
				wrappers.add(new WoodTypeRecipeWrapper(recipe, woodType));
			}
		}
		registry.addRecipes(wrappers, VanillaRecipeCategoryUid.CRAFTING);
	}
}
