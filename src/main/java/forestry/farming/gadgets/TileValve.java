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
package forestry.farming.gadgets;

import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FakeTank;
import forestry.core.interfaces.ILiquidTankContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

public class TileValve extends TileFarm implements ILiquidTankContainer {

	public TileValve() {
		fixedType = TYPE_VALVE;
	}

	/* TILEFARM */
	@Override
	public boolean hasFunction() {
		return true;
	}

	/* ILIQUIDTANKCONTAINER */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		TankManager tankManager = getTankManager();
		if (tankManager == null)
			return FakeTank.INSTANCE.fill(resource, doFill);

		return tankManager.fill(from, resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		TankManager tankManager = getTankManager();
		if (tankManager == null)
			return null;

		return tankManager.drain(from, resource, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		TankManager tankManager = getTankManager();
		if (tankManager == null)
			return FakeTank.INSTANCE.drain(maxDrain, doDrain);

		return tankManager.drain(from, maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		TankManager tankManager = getTankManager();
		if (tankManager == null)
			return true;

		return tankManager.canFill(from, fluid);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		TankManager tankManager = getTankManager();
		if (tankManager == null)
			return false;

		return tankManager.canDrain(from, fluid);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		TankManager tankManager = getTankManager();
		if (tankManager == null)
			return FakeTank.INFO;

		return tankManager.getTankInfo(from);
	}

	@Override
	public TankManager getTankManager() {
		TileFarmPlain central = (TileFarmPlain) getCentralTE();
		if (central == null)
			return null;

		return central.getTankManager();
	}

	@Override
	public void getGUINetworkData(int messageId, int data) {
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
	}
}
