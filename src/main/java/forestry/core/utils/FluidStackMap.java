/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.utils;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidStackMap<T> extends StackMap<FluidStack, T> {
	private static final long serialVersionUID = -3314134471346370713L;

	@Override
	protected boolean areEqual(FluidStack a, Object b) {

		if(b instanceof FluidStack)
			return a.isFluidEqual((FluidStack) b);
		if(b instanceof Fluid) {
			return ((Fluid)b).getID() == a.getFluid().getID();
		} if(b instanceof String)
			return b.equals(a.getFluid().getName());
		return false;
	}

	@Override
	protected boolean isValidKey(Object key) {
		return key instanceof FluidStack || key instanceof Fluid || key instanceof String;
	}

	@Override
	protected FluidStack getStack(Object key) {
		if (key instanceof FluidStack)
			return (FluidStack) key;
		if (key instanceof Fluid)
			return FluidRegistry.getFluidStack(((Fluid) key).getName(), 1);
		if (key instanceof String)
			return FluidRegistry.getFluidStack((String) key, 1);
		return null;
	}

}
