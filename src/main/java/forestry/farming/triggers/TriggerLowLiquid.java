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
package forestry.farming.triggers;

import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidTank;

import forestry.core.fluids.ITankManager;
import forestry.core.triggers.Trigger;
import forestry.farming.tiles.TileFarmHatch;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;

public class TriggerLowLiquid extends Trigger {

	private final float threshold;

	public TriggerLowLiquid(String tag, float threshold) {
		super(tag, "lowLiquid");
		this.threshold = threshold;
	}

	@Override
	public String getDescription() {
		return super.getDescription() + " < " + threshold * 100 + "%";
	}

	/**
	 * Return true if the tile given in parameter activates the trigger, given
	 * the parameters.
	 */
	@Override
	public boolean isTriggerActive(TileEntity tile, ForgeDirection side, IStatementContainer source, IStatementParameter[] parameters) {
		if (!(tile instanceof TileFarmHatch)) {
			return false;
		}

		TileFarmHatch tileHatch = (TileFarmHatch) tile;
		ITankManager tankManager = tileHatch.getMultiblockLogic().getController().getTankManager();

		IFluidTank tank = tankManager.getTank(0);
		if (tank.getCapacity() == 0) {
			return false;
		}
		return ((float) tank.getFluidAmount() / tank.getCapacity()) <= threshold;
	}
}
