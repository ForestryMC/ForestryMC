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
import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotLiquidIn;
import forestry.core.network.packets.PacketGuiUpdate;
import forestry.energy.inventory.InventoryGenerator;
import forestry.energy.tiles.TileEuGenerator;

public class ContainerGenerator extends ContainerLiquidTanks<TileEuGenerator> {

	public ContainerGenerator(InventoryPlayer player, TileEuGenerator tile) {
		super(tile, player, 8, 84);

		this.addSlotToContainer(new SlotLiquidIn(tile, InventoryGenerator.SLOT_CAN, 22, 38));
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		PacketGuiUpdate packet = new PacketGuiUpdate(tile);
		sendPacketToListeners(packet);
	}
}
