package forestry.core.fluids;

import forestry.api.core.IFluidContainerPermission;
import net.minecraftforge.fluids.FluidStack;

public class FluidContainerHeatPermission implements IFluidContainerPermission {

	@Override
	public boolean contentsAllowed(FluidStack fluidStack) {
		return fluidStack.getFluid().getTemperature(fluidStack) < 310.15; // melting point of wax in kelvin
	}
	
}
