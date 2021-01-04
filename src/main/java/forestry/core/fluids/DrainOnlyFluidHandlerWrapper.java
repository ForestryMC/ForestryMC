package forestry.core.fluids;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class DrainOnlyFluidHandlerWrapper implements IFluidHandler {
	private final IFluidHandler internalFluidHandler;

	public DrainOnlyFluidHandlerWrapper(IFluidHandler internalFluidHandler) {
		this.internalFluidHandler = internalFluidHandler;
	}

	@Override
	public int getTanks() {
		return internalFluidHandler.getTanks();
	}

	@Nonnull
	@Override
	public FluidStack getFluidInTank(int tank) {
		return internalFluidHandler.getFluidInTank(tank);
	}

	@Override
	public int getTankCapacity(int tank) {
		return internalFluidHandler.getTankCapacity(tank);
	}

	@Override
	public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
		return internalFluidHandler.isFluidValid(tank, stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return 0;
	}

	@Nonnull
	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return internalFluidHandler.drain(resource, action);
	}

	@Nonnull
	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return internalFluidHandler.drain(maxDrain, action);
	}
}
