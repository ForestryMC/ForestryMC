//package forestry.modules.features;
//
//import javax.annotation.Nullable;
//
//import net.minecraftforge.fluids.FluidAttributes;
//import net.minecraftforge.fluids.FluidStack;
//
//import forestry.core.fluids.ForestryFluid;
//
//public interface IFluidFeature extends IModFeature<FluidType> {
//
//	default FluidType apply(FluidType fluid) {
//		return fluid;
//	}
//
//	void setFluid(ForestryFluid fluid);
//
//	@Nullable
//	ForestryFluid getFluid();
//
//	boolean hasFluid();
//
//	default ForestryFluid fluid() {
//		ForestryFluid block = getFluid();
//		if (block == null) {
//			throw new IllegalStateException("Called feature getter method before content creation.");
//		}
//		return block;
//	}
//
//	default FluidStack stack(int amount) {
//		if (hasFluid()) {
//			return new FluidStack(fluid(), amount);
//		}
//		return FluidStack.EMPTY;
//	}
//
//	default FluidStack stack() {
//		return stack(FluidAttributes.BUCKET_VOLUME);
//	}
//}
