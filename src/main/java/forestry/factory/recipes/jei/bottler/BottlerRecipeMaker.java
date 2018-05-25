package forestry.factory.recipes.jei.bottler;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import mezz.jei.api.ingredients.IIngredientRegistry;

public class BottlerRecipeMaker {

	private BottlerRecipeMaker() {
	}

	public static List<BottlerRecipeWrapper> getBottlerRecipes(IIngredientRegistry ingredientRegistry) {
		List<BottlerRecipeWrapper> recipes = new ArrayList<>();
		for (ItemStack stack : ingredientRegistry.getAllIngredients(ItemStack.class)) {
			if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
				IFluidHandlerItem fluidHandler = stack.copy().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
				if (fluidHandler != null) {
					final boolean canDrain = canDrain(fluidHandler);
					final boolean canFill = canFill(fluidHandler);

					if (canDrain) {
						FluidStack drainedFluid = fluidHandler.drain(Integer.MAX_VALUE, true);
						if (drainedFluid != null) {
							ItemStack drained = fluidHandler.getContainer();
							recipes.add(new BottlerRecipeWrapper(stack, drainedFluid, drained, false));
						}
					}

					if (canFill) {
						for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
							IFluidHandlerItem fillingCapability = stack.copy().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
							if (fillingCapability != null) {
								int fill = fillingCapability.fill(new FluidStack(fluid, Integer.MAX_VALUE), true);
								if (fill > 0) {
									FluidStack filledFluid = new FluidStack(fluid, fill);
									ItemStack filled = fillingCapability.getContainer();
									recipes.add(new BottlerRecipeWrapper(stack, filledFluid, filled, true));
								}
							} else {
								break;
							}
						}
					}
				}
			}
		}
		return recipes;
	}

	private static boolean canDrain(IFluidHandler fluidHandler) {
		for (IFluidTankProperties properties : fluidHandler.getTankProperties()) {
			if (properties.canDrain()) {
				return true;
			}
		}
		return false;
	}

	private static boolean canFill(IFluidHandler fluidHandler) {
		for (IFluidTankProperties properties : fluidHandler.getTankProperties()) {
			if (properties.canFill()) {
				return true;
			}
		}
		return false;
	}
}
