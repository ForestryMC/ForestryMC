//package forestry.factory.recipes.jei.fermenter;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.NonNullList;
//
////import net.minecraftforge.oredict.OreDictionary;
//
//import forestry.api.recipes.IFermenterRecipe;
//import forestry.api.recipes.IVariableFermentable;
//import forestry.api.recipes.RecipeManagers;
//import forestry.core.utils.Log;
//
////import mezz.jei.api.recipe.IStackHelper;
//
//public class FermenterRecipeMaker {
//
//	private FermenterRecipeMaker() {
//	}
//
////	public static List<FermenterRecipeWrapper> getFermenterRecipes(IStackHelper stackHelper) {
////		List<FermenterRecipeWrapper> recipes = new ArrayList<>();
////		for (IFermenterRecipe recipe : RecipeManagers.fermenterManager.recipes()) {
////			if (!recipe.getResource().isEmpty()) {
////				addWrapperToList(stackHelper, recipe, recipe.getResource(), recipes);
////			} else if (recipe.getResourceOreName() != null) {
////				NonNullList<ItemStack> itemStacks = OreDictionary.getOres(recipe.getResourceOreName());
////				if (!itemStacks.isEmpty()) {
////					for (ItemStack resource : itemStacks) {
////						addWrapperToList(stackHelper, recipe, resource, recipes);
////					}
////				}
////			} else {
////				Log.error("Empty resource for recipe");
////			}
////		}
////		return recipes;
////	}
////
////	private static void addWrapperToList(IStackHelper stackHelper, IFermenterRecipe recipe, ItemStack resource, List<FermenterRecipeWrapper> recipes) {
////		if (resource.getItem() instanceof IVariableFermentable) {
////			for (ItemStack stack : stackHelper.getSubtypes(resource)) {
////				recipes.add(new FermenterRecipeWrapper(recipe, stack));
////			}
////		} else {
////			recipes.add(new FermenterRecipeWrapper(recipe, resource));
////		}
////	}
//
//}
