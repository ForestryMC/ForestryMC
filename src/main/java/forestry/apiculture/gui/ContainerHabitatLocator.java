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
package forestry.apiculture.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.apiculture.items.ItemHabitatLocator.HabitatLocatorInventory;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;

public class ContainerHabitatLocator extends ContainerItemInventory {

	public ContainerHabitatLocator(InventoryPlayer inventoryplayer, HabitatLocatorInventory inventory) {
		super(inventory, inventoryplayer.player);

		// Energy
		this.addSlotToContainer(new SlotFiltered(inventory, 2, 152, 8));

		// Bee to analyze
		this.addSlotToContainer(new SlotFiltered(inventory, 0, 152, 32));
		// Analyzed bee
		this.addSlotToContainer(new SlotOutput(inventory, 1, 152, 75));

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++) {
			for (int l1 = 0; l1 < 9; l1++) {
				addSecuredSlot(inventoryplayer, l1 + i1 * 9 + 9, 8 + l1 * 18, 102 + i1 * 18);
			}
		}
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			addSecuredSlot(inventoryplayer, j1, 8 + j1 * 18, 160);
		}
	}
}
