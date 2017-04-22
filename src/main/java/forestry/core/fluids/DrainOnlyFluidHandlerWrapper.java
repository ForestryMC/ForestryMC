package forestry.core.fluids;

import javax.annotation.Nullable;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class DrainOnlyFluidHandlerWrapper implements IFluidHandler {
	private final IFluidHandler internalFluidHandler;

	public DrainOnlyFluidHandlerWrapper(IFluidHandler internalFluidHandler) {
		this.internalFluidHandler = internalFluidHandler;
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		IFluidTankProperties[] internalTankProperties = internalFluidHandler.getTankProperties();
		IFluidTankProperties[] tankProperties = new IFluidTankProperties[internalTankProperties.length];
		for (int i = 0; i < internalTankProperties.length; i++) {
			IFluidTankProperties fluidTankProperties = internalTankProperties[i];
			tankProperties[i] = new DrainOnlyFluidPropertiesWrapper(fluidTankProperties);
		}
		return tankProperties;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		return 0;
	}

	@Nullable
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		return internalFluidHandler.drain(resource, doDrain);
	}

	@Nullable
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		return internalFluidHandler.drain(maxDrain, doDrain);
	}

	private static class DrainOnlyFluidPropertiesWrapper implements IFluidTankProperties {

		private final IFluidTankProperties internalTankProperties;

		public DrainOnlyFluidPropertiesWrapper(IFluidTankProperties internalTankProperties) {
			this.internalTankProperties = internalTankProperties;
		}

		@Nullable
		@Override
		public FluidStack getContents() {
			return internalTankProperties.getContents();
		}

		@Override
		public int getCapacity() {
			return internalTankProperties.getCapacity();
		}

		@Override
		public boolean canFill() {
			return false;
		}

		@Override
		public boolean canDrain() {
			return internalTankProperties.canDrain();
		}

		@Override
		public boolean canFillFluidType(FluidStack fluidStack) {
			return false;
		}

		@Override
		public boolean canDrainFluidType(FluidStack fluidStack) {
			return internalTankProperties.canDrainFluidType(fluidStack);
		}
	}
}
