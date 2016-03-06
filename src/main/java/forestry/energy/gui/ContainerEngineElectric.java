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
package forestry.energy.gui;

import forestry.core.network.packets.PacketGuiUpdate;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

import forestry.core.gui.ContainerSocketed;
import forestry.core.gui.slots.SlotFiltered;
import forestry.energy.tiles.InventoryEngineElectric;
import forestry.energy.tiles.TileEngineElectric;

public class ContainerEngineElectric extends ContainerSocketed<TileEngineElectric> {

	public ContainerEngineElectric(InventoryPlayer player, TileEngineElectric tile) {
		super(tile, player, 8, 84);

		this.addSlotToContainer(new SlotFiltered(tile, InventoryEngineElectric.SLOT_BATTERY, 84, 53));
	}

	@Override
	public void updateProgressBar(int i, int j) {
		if (tile != null) {
			tile.getGUINetworkData(i, j);
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (tile != null) {
			PacketGuiUpdate packet = new PacketGuiUpdate(tile);
			sendPacketToCrafters(packet);
		}
	}

}
