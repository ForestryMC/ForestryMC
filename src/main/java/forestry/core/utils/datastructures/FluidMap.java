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
package forestry.core.utils.datastructures;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidMap<T> extends StackMap<Fluid, T> {
	private static final long serialVersionUID = 15891293315299994L;

	@Override
	protected boolean areEqual(Fluid a, Object b) {
		if (b instanceof FluidStack) {
			return ((FluidStack) b).getFluid() == a;
		}
		if (b instanceof Fluid) {
			return b == a;
		}
		if (b instanceof String) {
			return b.equals(a.getName());
		}
		return false;
	}

	@Override
	protected boolean isValidKey(Object key) {
		return key instanceof FluidStack || key instanceof Fluid || key instanceof String;
	}

	@Override
	protected Fluid getStack(Object key) {
		if (key instanceof FluidStack) {
			return ((FluidStack) key).getFluid();
		}
		if (key instanceof Fluid) {
			return (Fluid) key;
		}
		if (key instanceof String) {
			return FluidRegistry.getFluid((String) key);
		}
		return null;
	}
}
