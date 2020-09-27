package forestry.factory.recipes.jei.carpenter;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CarpenterRecipeWrapper extends ForestryRecipeWrapper<ICarpenterRecipe> {
    private final List<Ingredient> ingredients;

    public CarpenterRecipeWrapper(ICarpenterRecipe recipe) {
        super(recipe);

        ShapedRecipe craftingGridRecipe = recipe.getCraftingGridRecipe();
        NonNullList<Ingredient> inputs = craftingGridRecipe.getIngredients();

        this.ingredients = new ArrayList<>();
        for (Ingredient ingredient : inputs) {
            this.ingredients.add(ingredient);
        }
    }

    @Override
    public void setIngredients(IIngredients ingredients) {
        ICarpenterRecipe recipe = getRecipe();
        ShapedRecipe craftingGridRecipe = recipe.getCraftingGridRecipe();

        List<Ingredient> inputStacks = new ArrayList<>();
        Ingredient box = recipe.getBox();
        if (!box.hasNoMatchingItems()) {
            inputStacks.add(box);
        }

        ingredients.setInputIngredients(inputStacks);

        FluidStack fluidResource = recipe.getFluidResource();
        if (fluidResource != null) {
            ingredients.setInputs(VanillaTypes.FLUID, Collections.singletonList(fluidResource));
        }

        ItemStack recipeOutput = craftingGridRecipe.getRecipeOutput();
        ingredients.setOutput(VanillaTypes.ITEM, recipeOutput);
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }
}
