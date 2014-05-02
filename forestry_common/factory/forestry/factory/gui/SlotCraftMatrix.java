/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.factory.gui;

import net.minecraft.inventory.IInventory;

import forestry.core.gui.slots.SlotForestry;
import forestry.core.interfaces.IContainerCrafting;

/**
 * Informs the passed container of slot changes. Contains a dummy itemstack.
 */
public class SlotCraftMatrix extends SlotForestry {

	private IContainerCrafting eventHandler;
	private int slot;

	public SlotCraftMatrix(IContainerCrafting container, IInventory iinventory, int i, int j, int k) {
		super(iinventory, i, j, k);
		setPhantom();
		this.eventHandler = container;
		this.slot = i;
		stackLimit = 1;
	}

	@Override
	public void onSlotChanged() {
		super.onSlotChanged();
		eventHandler.onCraftMatrixChanged(inventory, slot);
	}

}
