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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

import forestry.api.circuits.ICircuitLayout;
import forestry.core.circuits.ItemSolderingIron.SolderingInventory;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.IGuiSelectable;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;

public class ContainerSolderingIron extends ContainerItemInventory<SolderingInventory> implements IGuiSelectable {

	public ContainerSolderingIron(InventoryPlayer inventoryplayer, SolderingInventory inventory) {
		super(inventory);

		// Input
		this.addSlotToContainer(new SlotFiltered(inventory, 0, 152, 12));

		// Output
		this.addSlotToContainer(new SlotOutput(inventory, 1, 152, 92));

		// Ingredients
		this.addSlotToContainer(new SlotFiltered(inventory, 2, 12, 32));
		this.addSlotToContainer(new SlotFiltered(inventory, 3, 12, 52));
		this.addSlotToContainer(new SlotFiltered(inventory, 4, 12, 72));
		this.addSlotToContainer(new SlotFiltered(inventory, 5, 12, 92));

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++) {
			for (int l1 = 0; l1 < 9; l1++) {
				addSecuredSlot(inventoryplayer, l1 + i1 * 9 + 9, 8 + l1 * 18, 123 + i1 * 18);
			}
		}
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			addSecuredSlot(inventoryplayer, j1, 8 + j1 * 18, 181);
		}

	}

	public ICircuitLayout getLayout() {
		return inventory.getLayout();
	}

	public static void advanceSelection(int index) {
		PacketPayload payload = new PacketPayload(2, 0, 0);
		payload.intPayload[0] = index;
		payload.intPayload[1] = 0;
		sendSelectionChange(payload);
	}

	public static void regressSelection(int index) {
		PacketPayload payload = new PacketPayload(2, 0, 0);
		payload.intPayload[0] = index;
		payload.intPayload[1] = 1;
		sendSelectionChange(payload);
	}

	private static void sendSelectionChange(PacketPayload payload) {
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
			if (packet.payload.intPayload[0] == 0) {
				inventory.advanceLayout();
			}

		} else if (packet.payload.intPayload[0] == 0) {
			inventory.regressLayout();
		}

		sendSelection(player);
	}

	public void sendSelection(EntityPlayer player) {
		PacketPayload payload = new PacketPayload(0, 0, 1);
		payload.stringPayload[0] = inventory.getLayout().getUID();
		Proxies.net.sendToPlayer(new PacketUpdate(PacketIds.GUI_SELECTION, payload), player);
	}

}
