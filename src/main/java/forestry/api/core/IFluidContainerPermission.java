package forestry.api.core;

import net.minecraftforge.fluids.FluidStack;

public interface IFluidContainerPermission {

	boolean contentsAllowed(FluidStack fluidStack);
	
}
