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
package forestry.core.gui.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import forestry.core.interfaces.IContainerCrafting;

/**
 * Informs the passed container of slot changes.
 */
public class SlotCraftAuto extends Slot {

	private final IContainerCrafting eventHandler;
	private final int slot;

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
