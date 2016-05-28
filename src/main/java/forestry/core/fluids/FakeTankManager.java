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

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

import forestry.core.fluids.tanks.FakeTank;

public class FakeTankManager implements ITankManager {
	public static final FakeTankManager instance = new FakeTankManager();

	private FakeTankManager() {

	}

	@Override
	public void containerAdded(Container container, IContainerListener player) {

	}

	@Override
	public void updateGuiData(Container container, List<IContainerListener> crafters) {

	}

	@Override
	public void containerRemoved(Container container) {

	}

	@Override
	public IFluidTank getTank(int tankIndex) {
		return FakeTank.INSTANCE;
	}

	@Override
	public boolean accepts(Fluid fluid) {
		return false;
	}

	@Override
	public void processTankUpdate(int tankIndex, FluidStack contents) {

	}

	@Override
	public FluidTankInfo[] getTankInfo() {
		return FakeTank.INFO;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		return FakeTank.INSTANCE.fill(resource, doFill);
	}

	@Nullable
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		return FakeTank.INSTANCE.drain(resource, doDrain);
	}

	@Nullable
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		return FakeTank.INSTANCE.drain(maxDrain, doDrain);
	}
}
