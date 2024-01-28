package forestry.factory.recipes.jei.bottler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.registries.ForgeRegistries;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IIngredientManager;

public class BottlerRecipeMaker {
	public static List<BottlerRecipe> getBottlerRecipes(IIngredientManager ingredientManager) {
		List<BottlerRecipe> recipes = new ArrayList<>();
		for (ItemStack stack : ingredientManager.getAllIngredients(VanillaTypes.ITEM_STACK)) {
			Optional<IFluidHandlerItem> drainFluidHandler = stack.copy()
					.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null)
					.resolve();
			if (drainFluidHandler.isPresent()) {
				addDrainRecipes(recipes, drainFluidHandler.get(), stack);
				addFillRecipes(recipes, stack);
			}
		}

		return recipes;
	}

	private static void addDrainRecipes(List<BottlerRecipe> recipes, IFluidHandlerItem fluidHandler, ItemStack stack) {
		if (Items.BUCKET.equals(stack.getItem())) {
			return;
		}

		FluidStack drainedFluid = fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
		if (drainedFluid.isEmpty()) {
			for (Fluid fluid : ForgeRegistries.FLUIDS.getValues()) {
				IFluidHandlerItem currentFluidHandler = stack.copy()
						.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null)
						.resolve()
						.orElseThrow();

				int simulateFill = currentFluidHandler.fill(new FluidStack(fluid, Integer.MAX_VALUE), IFluidHandler.FluidAction.SIMULATE);
				if (simulateFill > 0) {
					currentFluidHandler.fill(new FluidStack(fluid, Integer.MAX_VALUE), IFluidHandler.FluidAction.EXECUTE);
					addDrainRecipe(recipes, currentFluidHandler, currentFluidHandler.getContainer().copy());
				}
			}
		} else {
			addDrainRecipe(recipes, fluidHandler, stack);
		}
	}

	private static void addDrainRecipe(List<BottlerRecipe> recipes, IFluidHandlerItem fluidHandler, ItemStack stack) {
		FluidStack drainedFluid = fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
		if (!drainedFluid.isEmpty() && drainedFluid.getAmount() > 0) {
			ItemStack drained = fluidHandler.getContainer();
			if (drained.getItem() == Items.AIR) {
				drained = null;
			}

			recipes.add(new BottlerRecipe(stack, drainedFluid, drained, false));
		}
	}

	private static void addFillRecipes(List<BottlerRecipe> recipes, ItemStack stack) {
		for (Fluid fluid : ForgeRegistries.FLUIDS.getValues()) {
			IFluidHandlerItem currentFluidHandler = stack.copy()
					.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null)
					.resolve()
					.orElseThrow();

			//try to reduce itemstack copies
			int simulateFill = currentFluidHandler.fill(new FluidStack(fluid, Integer.MAX_VALUE), IFluidHandler.FluidAction.SIMULATE);
			if (simulateFill > 0) {
				FluidStack filledFluid = new FluidStack(fluid, simulateFill);
				currentFluidHandler.fill(new FluidStack(fluid, Integer.MAX_VALUE), IFluidHandler.FluidAction.EXECUTE);
				ItemStack filled = currentFluidHandler.getContainer();
				if (filled.getItem() == Items.AIR) {
					filled = null;
				}

				recipes.add(new BottlerRecipe(stack, filledFluid, filled, true));
			}
		}
	}
}
