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

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface ITankManager extends IFluidHandler {
	void containerAdded(Container container, IContainerListener crafter);

	void sendTankUpdate(Container container, List<IContainerListener> crafters);

	void containerRemoved(Container container);

	IFluidTank getTank(int tankIndex);

	boolean canFillFluidType(FluidStack fluidStack);

	/**
	 * For updating tanks on the client
	 */
	@OnlyIn(Dist.CLIENT)
	void processTankUpdate(int tankIndex, @Nullable FluidStack contents);
}
