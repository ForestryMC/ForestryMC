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

import buildcraft.api.gates.ITriggerParameter;

import forestry.api.core.ITileStructure;
import forestry.core.triggers.Trigger;
import forestry.core.fluids.TankManager;
import forestry.farming.gadgets.TileFarmPlain;
import forestry.farming.gadgets.TileHatch;
import net.minecraftforge.fluids.FluidTankInfo;

public class TriggerLowLiquid extends Trigger {

	private float threshold = 0.25F;

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
	public boolean isTriggerActive(ForgeDirection direction, TileEntity tile, ITriggerParameter parameter) {
		if (!(tile instanceof TileHatch))
			return false;

		ITileStructure central = ((TileHatch) tile).getCentralTE();
		if (central == null || !(central instanceof TileFarmPlain))
			return false;

		TankManager tankManager = ((TileFarmPlain) central).getTankManager();
		FluidTankInfo info = tankManager.getTankInfo(0);
		return ((float) info.fluid.amount / info.capacity) <= threshold;
	}
}
