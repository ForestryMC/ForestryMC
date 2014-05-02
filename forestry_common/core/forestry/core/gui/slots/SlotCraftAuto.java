/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gui.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import forestry.core.interfaces.IContainerCrafting;

/**
 * Informs the passed container of slot changes.
 */
public class SlotCraftAuto extends Slot {

	private IContainerCrafting eventHandler;
	private int slot;

	public SlotCraftAuto(IContainerCrafting container, IInventory iinventory, int slotNumber, int x, int y) {
		super(iinventory, slotNumber, x, y);
		this.eventHandler = container;
		this.slot = slotNumber;
	}

	@Override
	public void onSlotChanged() {
		super.onSlotChanged();
		eventHandler.onCraftMatrixChanged(inventory, slot);
	}

}
