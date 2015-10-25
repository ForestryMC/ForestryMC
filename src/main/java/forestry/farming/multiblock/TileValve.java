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
package forestry.farming.multiblock;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import forestry.core.fluids.ITankManager;
import forestry.core.interfaces.ILiquidTankContainer;

public class TileValve extends TileFarm implements ILiquidTankContainer {

	/* ILIQUIDTANKCONTAINER */
	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		return getTankManager().fill(from, resource, doFill);
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		return getTankManager().drain(from, resource, doDrain);
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		return getTankManager().drain(from, maxDrain, doDrain);
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return getTankManager().canFill(from, fluid);
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return getTankManager().canDrain(from, fluid);
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return getTankManager().getTankInfo(from);
	}

	@Override
	public ITankManager getTankManager() {
		return getFarmController().getTankManager();
	}

	@Override
	public void getGUINetworkData(int messageId, int data) {
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
	}
}
