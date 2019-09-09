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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;

import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.inventory.ItemInventoryImprinter;
import forestry.apiculture.network.packets.PacketImprintSelectionResponse;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.IGuiSelectable;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.utils.NetworkUtil;

public class ContainerImprinter extends ContainerItemInventory<ItemInventoryImprinter> implements IGuiSelectable {

	//TODO dedupe this
	public static ContainerImprinter fromNetwork(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
		Hand hand = extraData.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND;
		PlayerEntity player = playerInv.player;
		ItemInventoryImprinter inv = new ItemInventoryImprinter(player, player.getHeldItem(hand));
		return new ContainerImprinter(windowId, player.inventory, inv);
	}

	public ContainerImprinter(int windowId, PlayerInventory inventoryplayer, ItemInventoryImprinter inventory) {
		super(windowId, inventory, inventoryplayer, 8, 103, ModuleApiculture.getContainerTypes().IMPRINTER);

		// Input
		this.addSlot(new SlotFiltered(inventory, 0, 152, 12));
		// Output
		this.addSlot(new SlotOutput(inventory, 1, 152, 72));
	}

	@Override
	public void handleSelectionRequest(ServerPlayerEntity player, int primary, int secondary) {
		if (primary == 0) {
			if (secondary == 0) {
				inventory.advancePrimary();
			} else {
				inventory.regressPrimary();
			}
		} else {
			if (secondary == 0) {
				inventory.advanceSecondary();
			} else {
				inventory.regressSecondary();
			}
		}

		PacketImprintSelectionResponse packetResponse = new PacketImprintSelectionResponse(inventory.getPrimaryIndex(), inventory.getSecondaryIndex());
		NetworkUtil.sendToPlayer(packetResponse, player);
	}

	public void setSelection(int primary, int secondary) {
		inventory.setPrimaryIndex(primary);
		inventory.setSecondaryIndex(secondary);
	}
}
