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

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;

public class FakeTankManager extends EmptyFluidHandler implements ITankManager {
	public static final FakeTankManager instance = new FakeTankManager();

	private FakeTankManager() {

	}

	@Override
	public void containerAdded(AbstractContainerMenu container, ContainerListener player) {

	}

	@Override
	public void sendTankUpdate(AbstractContainerMenu container, List<ContainerListener> crafters) {

	}

	@Override
	public void containerRemoved(AbstractContainerMenu container) {

	}

	@Override
	public IFluidTank getTank(int tankIndex) {
		return null;    //TODO return dummy tank instead?
	}

	@Override
	public boolean canFillFluidType(FluidStack fluidStack) {
		return false;
	}

	@Override
	public void processTankUpdate(int tankIndex, @Nullable FluidStack contents) {

	}
}
