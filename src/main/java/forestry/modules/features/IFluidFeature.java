package forestry.modules.features;

import javax.annotation.Nullable;
import java.util.function.Supplier;

import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;

import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import forestry.core.fluids.BlockForestryFluid;

public interface IFluidFeature extends IModFeature {

	FeatureBlock<BlockForestryFluid, BlockItem> fluidBlock();

	default Fluid apply(Fluid fluid) {
		return fluid;
	}

	void setFluid(FlowingFluid fluid);

	void setFlowing(FlowingFluid flowing);

	Supplier<FlowingFluid> getFluidConstructor(boolean flowing);

	@Nullable
	FlowingFluid getFluid();

	@Nullable
	FlowingFluid getFlowing();

	FluidProperties getProperties();

	boolean hasFluid();

	boolean hasFlowing();

	default FlowingFluid fluid() {
		FlowingFluid fluid = getFluid();
		if (fluid == null) {
			throw new IllegalStateException("Called feature getter method before content creation.");
		}
		return fluid;
	}

	default FlowingFluid flowing() {
		FlowingFluid flowing = getFlowing();
		if (flowing == null) {
			throw new IllegalStateException("Called feature getter method before content creation.");
		}
		return flowing;
	}

	default FluidStack fluidStack(int amount) {
		if (hasFluid()) {
			return new FluidStack(fluid(), amount);
		}
		return FluidStack.EMPTY;
	}

	default FluidStack fluidStack() {
		return fluidStack(FluidAttributes.BUCKET_VOLUME);
	}
}
