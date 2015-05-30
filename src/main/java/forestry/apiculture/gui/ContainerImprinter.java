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
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.apiculture.items.ItemImprinter.ImprinterInventory;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.IGuiSelectable;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.network.ForestryPacket;
import forestry.core.network.PacketGuiSelect;
import forestry.core.network.PacketId;
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

		if (!Proxies.common.isSimulating(entityplayer.worldObj)) {
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
		if (!isNetSynced && !Proxies.common.isSimulating(world)) {
			isNetSynced = true;
			Proxies.net.sendToServer(new ForestryPacket(PacketId.IMPRINT_SELECTION_GET));
		}
	}

	private void sendSelectionChange(int index, int advance) {
		ForestryPacket packet = new PacketGuiSelect(PacketId.GUI_SELECTION_CHANGE, index, advance);
		Proxies.net.sendToServer(packet);
		isNetSynced = false;
	}

	@Override
	public void handleSelectionChange(EntityPlayer player, PacketGuiSelect packet) {
		if (packet.getSecondaryIndex() == 0) {
			if (packet.getPrimaryIndex() == 0) {
				inventory.advancePrimary();
			} else {
				inventory.advanceSecondary();
			}
		} else if (packet.getPrimaryIndex() == 0) {
			inventory.regressPrimary();
		} else {
			inventory.regressSecondary();
		}
	}

	public void sendSelection(EntityPlayer player) {
		ForestryPacket packet = new PacketGuiSelect(PacketId.GUI_SELECTION_SET, inventory.getPrimaryIndex(), inventory.getSecondaryIndex());
		Proxies.net.sendToPlayer(packet, player);
	}

	@Override
	public void setSelection(PacketGuiSelect packetPayload) {
		inventory.setPrimaryIndex(packetPayload.getPrimaryIndex());
		inventory.setSecondaryIndex(packetPayload.getSecondaryIndex());
	}
}
