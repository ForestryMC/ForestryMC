/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.farming.gadgets;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.utils.ForestryTank;

public class TileValve extends TileFarm implements ILiquidTankContainer {

	public TileValve() {
		fixedType = TYPE_VALVE;
	}

	/* TILEFARM */
	@Override
	public boolean hasFunction() {
		return true;
	}

	@Override
	protected void createInventory() {
	}

	/* ILIQUIDTANKCONTAINER */
	private ForestryTank getMasterTank() {
		if (!isIntegratedIntoStructure() || !hasMaster())
			return null;

		TileFarmPlain central = (TileFarmPlain) getCentralTE();
		if (central == null)
			return null;

		return central.getTank();
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		ForestryTank tank = getMasterTank();
		if (tank == null)
			return 0;

		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		ForestryTank tank = getMasterTank();
		if (tank == null)
			return null;

		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public ForestryTank[] getTanks() {
		ForestryTank tank = getMasterTank();
		if (tank == null)
			return ForestryTank.FAKETANK_ARRAY;

		return new ForestryTank[] { tank };
	}

}
