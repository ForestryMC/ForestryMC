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
package forestry.greenhouse.gui;

import forestry.core.gui.ContainerTile;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.gui.slots.SlotWorking;
import forestry.core.network.packets.PacketGuiUpdate;
import forestry.greenhouse.inventory.InventoryGreenhouseNursery;
import forestry.greenhouse.tiles.TileGreenhouseNursery;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerGreenhouseNursery extends ContainerTile<TileGreenhouseNursery> {

	public ContainerGreenhouseNursery(InventoryPlayer playerInventory, TileGreenhouseNursery tile) {
		super(tile, playerInventory, 8, 94);
		
		// Input buffer
		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 2; k++) {
				addSlotToContainer(new SlotFiltered(tile, InventoryGreenhouseNursery.SLOT_INPUT_1 + i * 2 + k, 8 + k * 18, 28 + i * 18));
			}
		}

		// Analyze slot
		addSlotToContainer(new SlotWorking(tile, InventoryGreenhouseNursery.SLOT_WORK, 84, 45));

		// Output buffer
		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 2; k++) {
				addSlotToContainer(new SlotOutput(tile, InventoryGreenhouseNursery.SLOT_OUTPUT_1 + i * 2 + k, 134 + k * 18, 28 + i * 18));
			}
		}
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		PacketGuiUpdate packet = new PacketGuiUpdate(tile);
		sendPacketToListeners(packet);
	}

}
