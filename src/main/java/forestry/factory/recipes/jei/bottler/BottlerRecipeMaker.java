package forestry.factory.recipes.jei.bottler;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import forestry.core.config.Constants;
import mezz.jei.api.ingredients.IIngredientRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class BottlerRecipeMaker {

	private BottlerRecipeMaker() {
	}

	public static List<BottlerRecipeWrapper> getBottlerRecipes(IIngredientRegistry ingredientRegistry) {
		List<BottlerRecipeWrapper> recipes = new ArrayList<>();

		ImmutableList<ItemStack> ingredients = ingredientRegistry.getIngredients(ItemStack.class);

		for (ItemStack ingredient : ingredients) {
			if (ingredient.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
				FluidStack drain;

				ItemStack emptyStack = ingredient.copy();
				IFluidHandler emptyCapability = emptyStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
				drain = emptyCapability.drain(Constants.PROCESSOR_TANK_CAPACITY, true);
				if (drain != null) {
					if (emptyStack.stackSize == 0) {
						emptyStack = null;
					}
					recipes.add(new BottlerRecipeWrapper(ingredient, drain, emptyStack, false));
					addFillRecipe(ingredient, drain.getFluid(), recipes);
				} else {
					for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
						addFillRecipe(ingredient, fluid, recipes);
					}
				}
			}
		}
		return recipes;
	}

	private static void addFillRecipe(ItemStack ingredient, Fluid fluid, List<BottlerRecipeWrapper> recipes) {
		ItemStack fillStack = ingredient.copy();
		IFluidHandler fillCapability = fillStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
		int fill = fillCapability.fill(new FluidStack(fluid, Constants.PROCESSOR_TANK_CAPACITY), true);
		if (fill > 0) {
			FluidStack filledFluid = new FluidStack(fluid, fill);
			recipes.add(new BottlerRecipeWrapper(ingredient, filledFluid, fillStack, true));
		}
	}
}
