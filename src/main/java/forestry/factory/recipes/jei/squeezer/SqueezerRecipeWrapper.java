package forestry.factory.recipes.jei.squeezer;

import forestry.api.recipes.ISqueezerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class SqueezerRecipeWrapper extends AbstractSqueezerRecipeWrapper<ISqueezerRecipe> {
    public SqueezerRecipeWrapper(ISqueezerRecipe recipe) {
        super(recipe);
    }

    @Override
    public void setIngredients(IIngredients ingredients) {
        NonNullList<ItemStack> resources = getRecipe().getResources();
        ingredients.setInputs(VanillaTypes.ITEM, resources);

        ItemStack remnants = getRecipe().getRemnants();
        if (!remnants.isEmpty()) {
            ingredients.setOutput(VanillaTypes.ITEM, remnants);
        }

        ingredients.setOutput(VanillaTypes.FLUID, getRecipe().getFluidOutput());
    }

    @Override
    public float getRemnantsChance() {
        return getRecipe().getRemnantsChance();
    }
}
