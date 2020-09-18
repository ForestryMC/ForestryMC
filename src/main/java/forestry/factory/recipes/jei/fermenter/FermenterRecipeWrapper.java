package forestry.factory.recipes.jei.fermenter;

import forestry.api.fuels.FermenterFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.IVariableFermentable;
import forestry.core.recipes.jei.ForestryRecipeWrapper;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FermenterRecipeWrapper extends ForestryRecipeWrapper<IFermenterRecipe> {
    private final ItemStack fermentable;

    public FermenterRecipeWrapper(IFermenterRecipe recipe, ItemStack fermentable) {
        super(recipe);
        this.fermentable = fermentable;
    }

    @Override
    public void setIngredients(IIngredients ingredients) {
        List<ItemStack> fuelInputs = new ArrayList<>();
        for (FermenterFuel fuel : FuelManager.fermenterFuel.values()) {
            fuelInputs.add(fuel.getItem());
        }

        ingredients.setInputLists(VanillaTypes.ITEM, Arrays.asList(
                Collections.singletonList(fermentable),
                fuelInputs
        ));

        FluidStack fluidInput = getRecipe().getFluidResource().copy();
        fluidInput.setAmount(getRecipe().getFermentationValue());
        ingredients.setInput(VanillaTypes.FLUID, fluidInput);

        int amount = Math.round(getRecipe().getFermentationValue() * getRecipe().getModifier());
        if (fermentable.getItem() instanceof IVariableFermentable) {
            amount *= ((IVariableFermentable) fermentable.getItem()).getFermentationModifier(fermentable);
        }

        FluidStack fluidOutput = new FluidStack(getRecipe().getOutput(), amount);
        ingredients.setOutput(VanillaTypes.FLUID, fluidOutput);
    }
}
