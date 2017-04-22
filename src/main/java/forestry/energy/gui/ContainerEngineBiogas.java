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

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotLiquidIn;
import forestry.core.network.packets.PacketGuiUpdate;
import forestry.energy.inventory.InventoryEngineBiogas;
import forestry.energy.tiles.TileEngineBiogas;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerEngineBiogas extends ContainerLiquidTanks<TileEngineBiogas> {

	public ContainerEngineBiogas(InventoryPlayer player, TileEngineBiogas engine) {
		super(engine, player, 8, 84);

		this.addSlotToContainer(new SlotLiquidIn(engine, InventoryEngineBiogas.SLOT_CAN, 143, 40));
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		PacketGuiUpdate packet = new PacketGuiUpdate(tile);
		sendPacketToListeners(packet);
	}
}
