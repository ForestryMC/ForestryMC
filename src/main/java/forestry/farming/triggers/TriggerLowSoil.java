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

import java.util.stream.Stream;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import forestry.api.farming.IFarmInventory;
import forestry.api.multiblock.IFarmController;
import forestry.core.triggers.Trigger;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.ItemStackUtil;
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

		IInventory resourcesInventory = farmInventory.getResourcesInventory();
		Stream<ItemStack> stackStream = InventoryUtil.getStacks(resourcesInventory).stream();

		if (parameter != null && !parameter.getItemStack().isEmpty()) {
			ItemStack filter = parameter.getItemStack();
			stackStream = stackStream.filter(s -> ItemStackUtil.areItemStacksEqualIgnoreCount(filter, s));
		}

		return stackStream
				.mapToInt(ItemStack::getCount)
				.sum() < threshold;
	}
}
