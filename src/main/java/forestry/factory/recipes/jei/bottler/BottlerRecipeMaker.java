package forestry.factory.recipes.jei.bottler;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class BottlerRecipeMaker {
    public static List<BottlerRecipeWrapper> getBottlerRecipes(IIngredientManager ingredientRegistry) {
        List<BottlerRecipeWrapper> recipes = new ArrayList<>();
        for (ItemStack stack : ingredientRegistry.getAllIngredients(VanillaTypes.ITEM)) {
            LazyOptional<IFluidHandlerItem> lazyDrainFluidHandler = stack.copy().getCapability(
                    CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,
                    null
            );
            if (lazyDrainFluidHandler.isPresent()) {
                addDrainRecipes(recipes, lazyDrainFluidHandler.orElse(null), stack);
                addFillRecipes(recipes, stack);
            }
        }

        return recipes;
    }

    private static void addDrainRecipes(
            List<BottlerRecipeWrapper> recipes,
            IFluidHandlerItem fluidHandler,
            ItemStack stack
    ) {
        if (Items.BUCKET.equals(stack.getItem())) {
            return;
        }

        FluidStack drainedFluid = fluidHandler.drain(
                Integer.MAX_VALUE,
                IFluidHandler.FluidAction.SIMULATE
        );
        if (drainedFluid.isEmpty()) {
            for (Fluid fluid : ForgeRegistries.FLUIDS.getValues()) {
                IFluidHandlerItem currentFluidHandler = stack.copy().getCapability(
                        CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,
                        null
                ).orElse(null);

                int simulateFill = currentFluidHandler.fill(
                        new FluidStack(fluid, Integer.MAX_VALUE),
                        IFluidHandler.FluidAction.SIMULATE
                );
                if (simulateFill > 0) {
                    currentFluidHandler.fill(
                            new FluidStack(fluid, Integer.MAX_VALUE),
                            IFluidHandler.FluidAction.EXECUTE
                    );
                    addDrainRecipe(recipes, currentFluidHandler, currentFluidHandler.getContainer().copy());
                }
            }
        } else {
            addDrainRecipe(recipes, fluidHandler, stack);
        }
    }

    private static void addDrainRecipe(
            List<BottlerRecipeWrapper> recipes,
            IFluidHandlerItem fluidHandler,
            ItemStack stack
    ) {
        FluidStack drainedFluid = fluidHandler.drain(
                Integer.MAX_VALUE,
                IFluidHandler.FluidAction.EXECUTE
        );
        if (!drainedFluid.isEmpty() && drainedFluid.getAmount() > 0) {
            ItemStack drained = fluidHandler.getContainer();
            if (drained.getItem() == Items.AIR) {
                drained = null;
            }

            recipes.add(new BottlerRecipeWrapper(stack, drainedFluid, drained, false));
        }
    }

    private static void addFillRecipes(List<BottlerRecipeWrapper> recipes, ItemStack stack) {
        for (Fluid fluid : ForgeRegistries.FLUIDS.getValues()) {
            IFluidHandlerItem currentFluidHandler = stack.copy().getCapability(
                    CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,
                    null
            ).orElse(null);

            //try to reduce itemstack copies
            int simulateFill = currentFluidHandler.fill(
                    new FluidStack(fluid, Integer.MAX_VALUE),
                    IFluidHandler.FluidAction.SIMULATE
            );
            if (simulateFill > 0) {
                FluidStack filledFluid = new FluidStack(fluid, simulateFill);
                currentFluidHandler.fill(
                        new FluidStack(fluid, Integer.MAX_VALUE),
                        IFluidHandler.FluidAction.EXECUTE
                );
                ItemStack filled = currentFluidHandler.getContainer();
                if (filled.getItem() == Items.AIR) {
                    filled = null;
                }

                recipes.add(new BottlerRecipeWrapper(stack, filledFluid, filled, true));
            }
        }
    }
}
