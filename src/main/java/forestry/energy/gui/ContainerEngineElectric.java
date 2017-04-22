/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http:www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.energy.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.gui.ContainerSocketed;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.network.packets.PacketGuiUpdate;
import forestry.energy.inventory.InventoryEngineElectric;
import forestry.energy.tiles.TileEngineElectric;

public class ContainerEngineElectric extends ContainerSocketed<TileEngineElectric> {

	public ContainerEngineElectric(InventoryPlayer player, TileEngineElectric tile) {
		super(tile, player, 8, 84);

		this.addSlotToContainer(new SlotFiltered(tile, InventoryEngineElectric.SLOT_BATTERY, 84, 53));
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		PacketGuiUpdate packet = new PacketGuiUpdate(tile);
		sendPacketToListeners(packet);
	}

}
