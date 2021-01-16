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
package forestry.worktable.inventory;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;

import forestry.core.gui.ContainerDummy;
import forestry.worktable.gui.ContainerWorktable;

public class CraftingInventoryForestry extends CraftingInventory {
	private final Container eventHandlerIn;

	public CraftingInventoryForestry(ContainerWorktable containerWorktable) {
		this(containerWorktable, 3, 3);
	}

	public CraftingInventoryForestry() {
		this(ContainerDummy.instance, 3, 3);
	}

	private CraftingInventoryForestry(Container eventHandlerIn, int width, int height) {
		super(eventHandlerIn, width, height);
		this.eventHandlerIn = eventHandlerIn;
	}

	public CraftingInventoryForestry copy() {
		CraftingInventoryForestry copy = new CraftingInventoryForestry(this.eventHandlerIn, getWidth(), getHeight());
		for (int slot = 0; slot < getSizeInventory(); slot++) {
			copy.setInventorySlotContents(slot, getStackInSlot(slot).copy());
		}
		return copy;
	}
}
