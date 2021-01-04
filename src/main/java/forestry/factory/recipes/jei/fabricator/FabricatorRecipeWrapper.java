package forestry.factory.recipes.jei.fabricator;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FabricatorRecipeWrapper extends ForestryRecipeWrapper<IFabricatorRecipe> {
    public FabricatorRecipeWrapper(IFabricatorRecipe recipe) {
        super(recipe);
    }

    @Override
    public void setIngredients(IIngredients ingredients) {
        IFabricatorRecipe recipe = getRecipe();

        NonNullList<NonNullList<ItemStack>> itemInputs = recipe.getIngredients();
        List<List<ItemStack>> inputStacks = new ArrayList<>();
        for (List<ItemStack> stacks : itemInputs) {
            List<ItemStack> copy = new ArrayList<>(stacks);
            inputStacks.add(copy);
        }

        ingredients.setInputLists(VanillaTypes.ITEM, inputStacks);

        ingredients.setInputs(VanillaTypes.FLUID, Collections.singletonList(getRecipe().getLiquid()));

        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
    }
}
