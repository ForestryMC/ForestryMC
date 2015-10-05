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

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import forestry.core.inventory.InvTools;
import forestry.core.triggers.Trigger;
import forestry.farming.multiblock.TileHatch;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;

public class TriggerLowFertilizer extends Trigger {

	private final float threshold;

	public TriggerLowFertilizer(String tag, float threshold) {
		super(tag, "lowFertilizer");
		this.threshold = threshold;
	}

	@Override
	public String getDescription() {
		return super.getDescription() + " < " + threshold * 100 + "%";
	}

	@Override
	public boolean isTriggerActive(TileEntity tile, EnumFacing side, IStatementContainer source, IStatementParameter[] parameters) {
		if (!(tile instanceof TileHatch)) {
			return false;
		}

		TileHatch tileHatch = (TileHatch) tile;
		IInventory fertilizerInventory = tileHatch.getFarmController().getFarmInventory().getFertilizerInventory();
		return InvTools.containsPercent(fertilizerInventory, threshold);
	}

	@Override
	public int getSheetLocation() {
		return 0;
	}
}
