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
import net.minecraft.inventory.Slot;

import forestry.apiculture.gadgets.TileAlvearySieve;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;

public class ContainerAlvearySieve extends ContainerForestry {

	public ContainerAlvearySieve(InventoryPlayer player, TileAlvearySieve tile) {
		super(tile);

		addSlotToContainer(new SlotOutput(tile, 0, 94, 52).setCrafter(tile));
		addSlotToContainer(new SlotOutput(tile, 1, 115, 39).setCrafter(tile));
		addSlotToContainer(new SlotOutput(tile, 2, 73, 39).setCrafter(tile));
		addSlotToContainer(new SlotOutput(tile, 3, 94, 26).setCrafter(tile));

		addSlotToContainer(new SlotFiltered(tile, TileAlvearySieve.SLOT_SIEVE, 43, 39).setCrafter(tile));

		// Player inventory
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + j * 18, 87 + i * 18));
			}
		}
		// Player hotbar
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(player, i, 8 + i * 18, 145));
		}

	}

}
