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

import forestry.apiculture.BeeHousingInventory;
import forestry.apiculture.multiblock.TileAlvearyPlain;
import forestry.core.gui.ContainerTile;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.network.PacketGuiUpdate;

public class ContainerAlveary extends ContainerTile<TileAlvearyPlain> {

	public ContainerAlveary(InventoryPlayer player, TileAlvearyPlain tile) {
		super(tile, player, 8, 108);

		// Queen/Princess
		this.addSlotToContainer(new SlotFiltered(tile, BeeHousingInventory.SLOT_QUEEN, 29, 39));

		// Drone
		this.addSlotToContainer(new SlotFiltered(tile, BeeHousingInventory.SLOT_DRONE, 29, 65));

		// Product Inventory
		this.addSlotToContainer(new SlotOutput(tile, 2, 116, 52));
		this.addSlotToContainer(new SlotOutput(tile, 3, 137, 39));
		this.addSlotToContainer(new SlotOutput(tile, 4, 137, 65));
		this.addSlotToContainer(new SlotOutput(tile, 5, 116, 78));
		this.addSlotToContainer(new SlotOutput(tile, 6, 95, 65));
		this.addSlotToContainer(new SlotOutput(tile, 7, 95, 39));
		this.addSlotToContainer(new SlotOutput(tile, 8, 116, 26));
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		PacketGuiUpdate packet = new PacketGuiUpdate(tile);
		sendPacketToCrafters(packet);
	}
}
