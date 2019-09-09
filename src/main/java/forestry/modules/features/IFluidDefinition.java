//package forestry.modules.features;
//
//import javax.annotation.Nullable;
//
//import net.minecraftforge.fluids.FluidAttributes;
//import net.minecraftforge.fluids.FluidStack;
//
//public interface IFluidDefinition extends IModFeature<FluidType> {
//
//	default FluidType apply(FluidType fluid) {
//		return fluid;
//	}
//
//	void setFluid(FluidType fluid);
//
//	@Nullable
//	FluidType getFluid();
//
//	boolean hasFluid();
//
//	default FluidType fluid() {
//		FluidType block = getFluid();
//		if (block == null) {
//			throw new IllegalStateException("Called feature getter method before content creation.");
//		}
//		return block;
//	}
//
//	default FluidStack stack(int amount) {
//		if (hasFluid()) {
//			return new FluidStack(fluid().getFluid(), amount);
//		}
//		return FluidStack.EMPTY;
//	}
//
//	default FluidStack stack() {
//		return stack(FluidAttributes.BUCKET_VOLUME);
//	}
//}
