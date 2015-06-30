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

import forestry.apiculture.multiblock.TileAlvearySieve;
import forestry.core.gui.ContainerTile;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;

public class ContainerAlvearySieve extends ContainerTile<TileAlvearySieve> {

	public ContainerAlvearySieve(InventoryPlayer player, TileAlvearySieve tile) {
		super(tile, player, 8, 87);

		addSlotToContainer(new SlotOutput(tile, 0, 94, 52).setCrafter(tile));
		addSlotToContainer(new SlotOutput(tile, 1, 115, 39).setCrafter(tile));
		addSlotToContainer(new SlotOutput(tile, 2, 73, 39).setCrafter(tile));
		addSlotToContainer(new SlotOutput(tile, 3, 94, 26).setCrafter(tile));

		addSlotToContainer(new SlotFiltered(tile, TileAlvearySieve.AlvearySieveInventory.SLOT_SIEVE, 43, 39).setCrafter(tile));
	}
}
