/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.utils;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidMap<T> extends StackMap<Fluid, T> {
	private static final long serialVersionUID = 15891293315299994L;

	@Override
	protected boolean areEqual(Fluid a, Object b) {
		if (b instanceof FluidStack)
			return ((FluidStack) b).getFluid() == a;
		if (b instanceof Fluid)
			return ((Fluid) b) == a;
		if (b instanceof String)
			return ((String) b).equals(a.getName());
		return false;
	}

	@Override
	protected boolean isValidKey(Object key) {
		return key instanceof FluidStack || key instanceof Fluid || key instanceof String;
	}

	@Override
	protected Fluid getStack(Object key) {
		if (key instanceof FluidStack)
			return ((FluidStack) key).getFluid();
		if (key instanceof Fluid)
			return (Fluid) key;
		if (key instanceof String)
			return FluidRegistry.getFluid((String) key);
		return null;
	}
}
