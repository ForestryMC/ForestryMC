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

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.farming.IFarmInventory;
import forestry.api.multiblock.IFarmController;
import forestry.core.triggers.Trigger;
import forestry.core.utils.InventoryUtil;
import forestry.farming.tiles.TileFarmHatch;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import buildcraft.api.statements.StatementParameterItemStack;

public class TriggerLowSoil extends Trigger {

	private final int threshold;

	public TriggerLowSoil(int threshold) {
		super("lowSoil." + threshold, "lowSoil");
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

	@Override
	public int minParameters() {
		return 0;
	}

	@Override
	public IStatementParameter createParameter(int index) {
		return new StatementParameterItemStack();
	}

	/**
	 * Return true if the tile given in parameter activates the trigger, given
	 * the parameters.
	 */
	@Override
	public boolean isTriggerActive(TileEntity tile, ForgeDirection side, IStatementContainer source, IStatementParameter[] parameters) {
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
		IInventory resourcesInventory = farmInventory.getResourcesInventory();

		if (parameter == null || parameter.getItemStack() == null) {
			return !InventoryUtil.containsAmount(resourcesInventory, threshold);
		} else {
			ItemStack filter = parameter.getItemStack().copy();
			filter.stackSize = threshold;
			return !InventoryUtil.contains(resourcesInventory, new ItemStack[]{filter});
		}
	}
}
