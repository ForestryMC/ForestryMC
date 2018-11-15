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
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;

import forestry.api.farming.IFarmInventory;
import forestry.api.multiblock.IFarmController;
import forestry.core.triggers.Trigger;
import forestry.core.utils.InventoryUtil;
import forestry.farming.tiles.TileFarmHatch;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;

public class TriggerLowSoil extends Trigger {

	private final int threshold;

	public TriggerLowSoil(int threshold) {
		super("lowSoil." + threshold, "lowSoil", "low_soil");
		this.threshold = threshold;
	}

	@Override
	public String getDescription() {
		return super.getDescription() + " < " + threshold;
	}

	@Override
	public int maxParameters() {
		return 1;
	}

	/**
	 * Return true if the tile given in parameter activates the trigger, given
	 * the parameters.
	 */
	@Override
	public boolean isTriggerActive(TileEntity tile, EnumFacing side, IStatementContainer source, IStatementParameter[] parameters) {
		IStatementParameter parameter = null;
		if (parameters.length > 0) {
			parameter = parameters[0];
		}

		if (!(tile instanceof TileFarmHatch)) {
			return false;
		}

		TileFarmHatch tileHatch = (TileFarmHatch) tile;
		IFarmController farmController = tileHatch.getMultiblockLogic().getController();
		IFarmInventory farmInventory = farmController.getFarmInventory();

		if (parameter == null || parameter.getItemStack().isEmpty()) {
			IInventory resourcesInventory = farmInventory.getResourcesInventory();
			return InventoryUtil.containsPercent(resourcesInventory, threshold);
		} else {
			ItemStack filter = parameter.getItemStack().copy();
			filter.setCount(threshold);
			return farmInventory.hasResources(NonNullList.from(ItemStack.EMPTY, filter));
		}
	}
}
