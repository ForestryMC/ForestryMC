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
package forestry.factory.inventory;

import net.minecraft.inventory.InventoryCrafting;

import forestry.core.gui.ContainerDummy;
import forestry.factory.gui.ContainerWorktable;

import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class InventoryCraftingForestry extends InventoryCrafting {
	private final Container eventHandlerIn;
	
	public InventoryCraftingForestry(ContainerWorktable containerWorktable) {
		this(containerWorktable, 3, 3);
	}

	public InventoryCraftingForestry() {
		this(ContainerDummy.instance, 3, 3);
	}
	
	private InventoryCraftingForestry(Container eventHandlerIn, int width, int height) {
		super(eventHandlerIn, width, height);
		this.eventHandlerIn = eventHandlerIn;
	}
	
	public InventoryCraftingForestry copy() {
		InventoryCraftingForestry copy = new InventoryCraftingForestry(this.eventHandlerIn, getWidth(), getHeight());
		for (int slot = 0; slot < getSizeInventory(); slot++) {
			ItemStack stackInSlot = getStackInSlot(slot);
			if (stackInSlot != null) {
				copy.setInventorySlotContents(slot, stackInSlot.copy());
			}
		}
		return copy;
	}
}
