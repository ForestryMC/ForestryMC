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

import forestry.apiculture.gadgets.TileAlvearySwarmer;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.slots.SlotFiltered;

public class ContainerAlvearySwarmer extends ContainerForestry {

	public ContainerAlvearySwarmer(InventoryPlayer player, TileAlvearySwarmer tile) {
		super(tile);

		this.addSlotToContainer(new SlotFiltered(tile, 0, 79, 52));
		this.addSlotToContainer(new SlotFiltered(tile, 1, 100, 39));
		this.addSlotToContainer(new SlotFiltered(tile, 2, 58, 39));
		this.addSlotToContainer(new SlotFiltered(tile, 3, 79, 26));

		// Player inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + j * 18, 87 + i * 18));
		// Player hotbar
		for (int i = 0; i < 9; i++)
			addSlotToContainer(new Slot(player, i, 8 + i * 18, 145));

	}
}
