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
package forestry.core.circuits;

import forestry.api.circuits.ICircuitLayout;
import forestry.core.circuits.ItemSolderingIron.SolderingInventory;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.IGuiSelectable;
import forestry.core.gui.slots.SlotCustom;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerSolderingIron extends ContainerItemInventory implements IGuiSelectable {

	SolderingInventory inventory;

	public ContainerSolderingIron(InventoryPlayer inventoryplayer, SolderingInventory inventory) {
		super(inventory, inventoryplayer.player);
		this.inventory = inventory;

		// Input
		this.addSlot(new SlotCustom(inventory, 0, 152, 12, ItemCircuitBoard.class));

		// Output
		this.addSlot(new SlotCustom(inventory, 1, 152, 92, ItemCircuitBoard.class));

		// Ingredients
		this.addSlot(new Slot(inventory, 2, 12, 32));
		this.addSlot(new Slot(inventory, 3, 12, 52));
		this.addSlot(new Slot(inventory, 4, 12, 72));
		this.addSlot(new Slot(inventory, 5, 12, 92));

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++)
			for (int l1 = 0; l1 < 9; l1++)
				addSecuredSlot(inventoryplayer, l1 + i1 * 9 + 9, 8 + l1 * 18, 123 + i1 * 18);
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++)
			addSecuredSlot(inventoryplayer, j1, 8 + j1 * 18, 181);

	}

	@Override
	protected boolean isAcceptedItem(EntityPlayer player, ItemStack stack) {
		return false;
	}

	/*
	 * @Override public void onCraftGuiClosed(EntityPlayer entityplayer) {
	 * 
	 * if (!Proxies.common.isSimulating(entityplayer.worldObj)) return;
	 * 
	 * // Drop everything still in there. for (int i = 0; i < inventory.getSizeInventory(); i++) { ItemStack stack = inventory.getStackInSlot(i); if (stack ==
	 * null) { continue; }
	 * 
	 * Proxies.common.dropItemPlayer(entityplayer, stack); inventory.setInventorySlotContents(i, null); }
	 * 
	 * }
	 */

	public ICircuitLayout getLayout() {
		return inventory.getLayout();
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
	}

	@Override
	public void setSelection(PacketUpdate packet) {
		inventory.setLayout(packet.payload.stringPayload[0]);
	}

	@Override
	public void handleSelectionChange(EntityPlayer player, PacketUpdate packet) {

		if (packet.payload.intPayload[1] == 0) {
			if (packet.payload.intPayload[0] == 0)
				inventory.advanceLayout();

		} else if (packet.payload.intPayload[0] == 0)
			inventory.regressLayout();

		sendSelection(player);
	}

	public void sendSelection(EntityPlayer player) {
		PacketPayload payload = new PacketPayload(0, 0, 1);
		payload.stringPayload[0] = inventory.getLayout().getUID();
		Proxies.net.sendToPlayer(new PacketUpdate(PacketIds.GUI_SELECTION, payload), player);
	}

}
