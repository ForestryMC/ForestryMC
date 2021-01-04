package forestry.factory.recipes.jei.squeezer;

import forestry.factory.recipes.ISqueezerContainerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import java.util.Collections;
import java.util.Optional;

public class SqueezerContainerRecipeWrapper extends AbstractSqueezerRecipeWrapper<ISqueezerContainerRecipe> {
    private final ItemStack filledContainer;

    public SqueezerContainerRecipeWrapper(ISqueezerContainerRecipe recipe, ItemStack filledContainer) {
        super(recipe);
        this.filledContainer = filledContainer;
    }

    @Override
    public void setIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Collections.singletonList(filledContainer));

        ItemStack remnants = getRecipe().getRemnants();
        if (!remnants.isEmpty()) {
            ingredients.setOutput(VanillaTypes.ITEM, remnants);
        }

        Optional<FluidStack> fluidContained = FluidUtil.getFluidContained(filledContainer);
        if (fluidContained.isPresent()) {
            ingredients.setOutput(VanillaTypes.FLUID, fluidContained.orElse(null));
        }
    }

    @Override
    public float getRemnantsChance() {
        return getRecipe().getRemnantsChance();
    }
}
