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

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotLiquidIn;
import forestry.core.network.packets.PacketGuiUpdate;
import forestry.core.tiles.TileUtil;
import forestry.energy.ModuleEnergy;
import forestry.energy.inventory.InventoryGenerator;
import forestry.energy.tiles.TileEuGenerator;

public class ContainerGenerator extends ContainerLiquidTanks<TileEuGenerator> {

	//TODO dedupe
	public static ContainerGenerator fromNetwork(int windowId, PlayerInventory inv, PacketBuffer extraData) {
		TileEuGenerator tile = TileUtil.getTile(inv.player.world, extraData.readBlockPos(), TileEuGenerator.class);
		return new ContainerGenerator(windowId, inv, tile);
	}

	public ContainerGenerator(int windowId, PlayerInventory player, TileEuGenerator tile) {
		super(windowId, ModuleEnergy.getContainerTypes().GENERATOR, player, tile, 8, 84);

		this.addSlot(new SlotLiquidIn(tile, InventoryGenerator.SLOT_CAN, 22, 38));
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		PacketGuiUpdate packet = new PacketGuiUpdate(tile);
		sendPacketToListeners(packet);
	}
}
