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
package forestry.food.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.food.inventory.ItemInventoryInfuser;

public class ContainerInfuser extends ContainerItemInventory<ItemInventoryInfuser> {

	public ContainerInfuser(InventoryPlayer inventoryplayer, ItemInventoryInfuser inventory) {
		super(inventory, inventoryplayer, 8, 103);

		// Input
		this.addSlotToContainer(new SlotFiltered(inventory, 0, 152, 12));

		// Output
		this.addSlotToContainer(new SlotOutput(inventory, 1, 152, 72));

		// Ingredients
		this.addSlotToContainer(new SlotFiltered(inventory, 2, 12, 12));
		this.addSlotToContainer(new SlotFiltered(inventory, 3, 12, 32));
		this.addSlotToContainer(new SlotFiltered(inventory, 4, 12, 52));
		this.addSlotToContainer(new SlotFiltered(inventory, 5, 12, 72));
	}
}
