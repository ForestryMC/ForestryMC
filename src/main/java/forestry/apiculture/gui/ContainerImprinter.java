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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.apiculture.items.ItemImprinter.ImprinterInventory;
import forestry.apiculture.network.PacketImprintSelectionRequest;
import forestry.apiculture.network.PacketImprintSelectionResponse;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.IGuiSelectable;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketGuiSelectRequest;
import forestry.core.proxy.Proxies;

public class ContainerImprinter extends ContainerItemInventory<ImprinterInventory> implements IGuiSelectable {

	private boolean isNetSynced = false;

	public ContainerImprinter(InventoryPlayer inventoryplayer, ImprinterInventory inventory) {
		super(inventory, inventoryplayer, 8, 103);

		// Input
		this.addSlotToContainer(new SlotFiltered(inventory, 0, 152, 12));
		// Output
		this.addSlotToContainer(new SlotOutput(inventory, 1, 152, 72));
	}

	@Override
	public void onContainerClosed(EntityPlayer entityplayer) {

		if (entityplayer.worldObj.isRemote) {
			return;
		}

		// Drop everything still in there.
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack == null) {
				continue;
			}

			Proxies.common.dropItemPlayer(entityplayer, stack);
			inventory.setInventorySlotContents(i, null);
		}
	}

	public void advanceSelection(int index) {
		sendSelectionChange(index, 0);
	}

	public void regressSelection(int index) {
		sendSelectionChange(index, 1);
	}

	public void updateContainer(World world) {
		if (!isNetSynced && world.isRemote) {
			isNetSynced = true;
			Proxies.net.sendToServer(new PacketImprintSelectionRequest());
		}
	}

	private void sendSelectionChange(int index, int advance) {
		IForestryPacketServer packet = new PacketGuiSelectRequest(index, advance);
		Proxies.net.sendToServer(packet);
		isNetSynced = false;
	}

	@Override
	public void handleSelectionRequest(EntityPlayerMP player, PacketGuiSelectRequest packetRequest) {
		if (packetRequest.getSecondaryIndex() == 0) {
			if (packetRequest.getPrimaryIndex() == 0) {
				inventory.advancePrimary();
			} else {
				inventory.advanceSecondary();
			}
		} else if (packetRequest.getPrimaryIndex() == 0) {
			inventory.regressPrimary();
		} else {
			inventory.regressSecondary();
		}

		sendSelection(player);
	}

	public void sendSelection(EntityPlayerMP player) {
		PacketImprintSelectionResponse packetResponse = new PacketImprintSelectionResponse(inventory.getPrimaryIndex(), inventory.getSecondaryIndex());
		Proxies.net.sendToPlayer(packetResponse, player);
	}

	public void setSelection(PacketImprintSelectionResponse packetPayload) {
		inventory.setPrimaryIndex(packetPayload.getPrimaryIndex());
		inventory.setSecondaryIndex(packetPayload.getSecondaryIndex());
	}
}
