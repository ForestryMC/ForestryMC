/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
