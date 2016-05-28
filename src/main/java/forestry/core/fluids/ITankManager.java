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
package forestry.core.fluids;

import java.util.List;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface ITankManager extends IFluidHandler {
	void containerAdded(Container container, IContainerListener crafter);

	void updateGuiData(Container container, List<IContainerListener> crafters);

	void containerRemoved(Container container);

	IFluidTank getTank(int tankIndex);

	boolean accepts(Fluid fluid);

	/**
	 * For updating tanks on the client
	 */
	void processTankUpdate(int tankIndex, FluidStack contents);
}
