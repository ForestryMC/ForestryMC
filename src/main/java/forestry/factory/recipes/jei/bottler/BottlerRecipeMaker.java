//package forestry.factory.recipes.jei.bottler;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import net.minecraft.item.ItemStack;
//
//import net.minecraft.fluid.Fluid;
//import net.minecraftforge.fluids.FluidRegistry;
//import net.minecraftforge.fluids.FluidStack;
//import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
//import net.minecraftforge.fluids.capability.IFluidHandler;
//import net.minecraftforge.fluids.capability.IFluidHandlerItem;
//import net.minecraftforge.fluids.capability.IFluidTankProperties;
//
//import mezz.jei.api.ingredients.IIngredientRegistry;
//import mezz.jei.api.ingredients.VanillaTypes;
//
//public class BottlerRecipeMaker {
//
//	private BottlerRecipeMaker() {
//	}
//
//	public static List<BottlerRecipeWrapper> getBottlerRecipes(IIngredientRegistry ingredientRegistry) {
//		List<BottlerRecipeWrapper> recipes = new ArrayList<>();
//		for (ItemStack stack : ingredientRegistry.getAllIngredients(VanillaTypes.ITEM)) {
//			if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
//				IFluidHandlerItem fluidHandler = stack.copy().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
//				if (fluidHandler != null) {
//
//					if (hasDrainProperty(fluidHandler)) {
//						FluidStack drainedFluid = fluidHandler.drain(Integer.MAX_VALUE, true);
//						if (drainedFluid != null) {
//							ItemStack drained = fluidHandler.getContainer();
//							recipes.add(new BottlerRecipeWrapper(stack, drainedFluid, drained, false));
//						}
//					}
//
//					if (hasFillProperty(fluidHandler)) {
//						IFluidHandlerItem fillingCapability = stack.copy().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
//						if (fillingCapability != null) {
//							for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
//								int testFill = fillingCapability.fill(new FluidStack(fluid, Integer.MAX_VALUE), false);    //try to reduce itemstack copies
//								if (testFill > 0) {
//									IFluidHandlerItem copiedCap = stack.copy().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
//									int fill = copiedCap.fill(new FluidStack(fluid, Integer.MAX_VALUE), true);
//									FluidStack filledFluid = new FluidStack(fluid, fill);
//									ItemStack filled = copiedCap.getContainer();
//									recipes.add(new BottlerRecipeWrapper(stack, filledFluid, filled, true));
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//		return recipes;
//	}
//
//	private static boolean hasDrainProperty(IFluidHandler fluidHandler) {
//		for (IFluidTankProperties properties : fluidHandler.getTankProperties()) {
//			if (properties.canDrain()) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	private static boolean hasFillProperty(IFluidHandler fluidHandler) {
//		for (IFluidTankProperties properties : fluidHandler.getTankProperties()) {
//			if (properties.canFill()) {
//				return true;
//			}
//		}
//		return false;
//	}
//}
