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
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.apiculture.items.ItemImprinter.ImprinterInventory;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.IGuiSelectable;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;

public class ContainerImprinter extends ContainerForestry implements IGuiSelectable {

	public final ImprinterInventory inventory;
	private boolean isNetSynced = false;

	public ContainerImprinter(InventoryPlayer inventoryplayer, ImprinterInventory inventory) {
		super(inventory);

		this.inventory = inventory;
		// Input
		this.addSlotToContainer(new SlotFiltered(inventory, 0, 152, 12));
		// Output
		this.addSlotToContainer(new SlotOutput(inventory, 1, 152, 72));

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++)
			for (int l1 = 0; l1 < 9; l1++)
				addSlotToContainer(new Slot(inventoryplayer, l1 + i1 * 9 + 9, 8 + l1 * 18, 103 + i1 * 18));
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++)
			addSlotToContainer(new Slot(inventoryplayer, j1, 8 + j1 * 18, 161));
	}

	@Override
	public void onContainerClosed(EntityPlayer entityplayer) {

		if (!Proxies.common.isSimulating(entityplayer.worldObj))
			return;

		// Drop everything still in there.
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack == null)
				continue;

			Proxies.common.dropItemPlayer(entityplayer, stack);
			inventory.setInventorySlotContents(i, null);
		}

	}

	public void advanceSelection(int index, World world) {
		PacketPayload payload = new PacketPayload(2, 0, 0);
		payload.intPayload[0] = index;
		payload.intPayload[1] = 0;
		sendSelectionChange(payload);
	}

	public void regressSelection(int index, World world) {
		PacketPayload payload = new PacketPayload(2, 0, 0);
		payload.intPayload[0] = index;
		payload.intPayload[1] = 1;
		sendSelectionChange(payload);
	}

	private void sendSelectionChange(PacketPayload payload) {
		PacketUpdate packet = new PacketUpdate(PacketIds.GUI_SELECTION_CHANGE, payload);
		Proxies.net.sendToServer(packet);
		isNetSynced = false;
	}

	@Override
	public void setSelection(PacketUpdate packet) {
		inventory.setPrimaryIndex(packet.payload.intPayload[0]);
		inventory.setSecondaryIndex(packet.payload.intPayload[1]);
	}

	public void updateContainer(World world) {
		if (!isNetSynced && !Proxies.common.isSimulating(world)) {
			isNetSynced = true;
			Proxies.net.sendToServer(new PacketUpdate(PacketIds.IMPRINT_SELECTION_GET));
		}
	}

	@Override
	public void handleSelectionChange(EntityPlayer player, PacketUpdate packet) {

		if (packet.payload.intPayload[1] == 0) {
			if (packet.payload.intPayload[0] == 0)
				inventory.advancePrimary();
			else
				inventory.advanceSecondary();

		} else if (packet.payload.intPayload[0] == 0)
			inventory.regressPrimary();
		else
			inventory.regressSecondary();
	}

	public void sendSelection(EntityPlayer player) {
		PacketPayload payload = new PacketPayload(2, 0, 0);
		payload.intPayload[0] = inventory.getPrimaryIndex();
		payload.intPayload[1] = inventory.getSecondaryIndex();
		Proxies.net.sendToPlayer(new PacketUpdate(PacketIds.GUI_SELECTION, payload), player);
	}
}
