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

import forestry.apiculture.inventory.InventoryAlvearySieve;
import forestry.apiculture.multiblock.TileAlvearySieve;
import forestry.core.gui.ContainerTile;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.inventory.watchers.ISlotPickupWatcher;

public class ContainerAlvearySieve extends ContainerTile<TileAlvearySieve> {

	public ContainerAlvearySieve(InventoryPlayer player, TileAlvearySieve tile) {
		super(tile, player, 8, 87);

		ISlotPickupWatcher crafter = tile.getCrafter();

		addSlotToContainer(new SlotOutput(tile, 0, 94, 52).setPickupWatcher(crafter));
		addSlotToContainer(new SlotOutput(tile, 1, 115, 39).setPickupWatcher(crafter));
		addSlotToContainer(new SlotOutput(tile, 2, 73, 39).setPickupWatcher(crafter));
		addSlotToContainer(new SlotOutput(tile, 3, 94, 26).setPickupWatcher(crafter));

		addSlotToContainer(new SlotFiltered(tile, InventoryAlvearySieve.SLOT_SIEVE, 43, 39).setPickupWatcher(crafter));
	}
}
