package forestry.factory.recipes.jei.moistener;

import forestry.api.fuels.MoistenerFuel;
import forestry.api.recipes.IMoistenerRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class MoistenerRecipeWrapper extends ForestryRecipeWrapper<IMoistenerRecipe> {
    private final MoistenerFuel fuel;

    public MoistenerRecipeWrapper(IMoistenerRecipe recipe, MoistenerFuel fuel) {
        super(recipe);
        this.fuel = fuel;
    }

    @Override
    public void setIngredients(IIngredients ingredients) {
        IMoistenerRecipe recipe = getRecipe();

        List<Ingredient> itemIngredients = new ArrayList<>();
        itemIngredients.add(recipe.getResource());
        itemIngredients.add(fuel.getResource());
        ingredients.setInputIngredients(itemIngredients);

        List<ItemStack> itemStackOutputs = new ArrayList<>();
        itemStackOutputs.add(recipe.getProduct());
        ItemStack fuelProduct = fuel.getProduct();
        itemStackOutputs.add(fuelProduct);
        ingredients.setOutputs(VanillaTypes.ITEM, itemStackOutputs);

        ingredients.setInput(VanillaTypes.FLUID, new FluidStack(Fluids.WATER, recipe.getTimePerItem() / 4));
    }

    public MoistenerFuel getFuel() {
        return fuel;
    }
}
