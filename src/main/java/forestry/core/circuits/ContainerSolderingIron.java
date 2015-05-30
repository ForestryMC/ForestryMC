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

import forestry.api.circuits.ICircuitLayout;
import forestry.core.circuits.ItemSolderingIron.SolderingInventory;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.IGuiSelectable;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.network.PacketGuiSelect;
import forestry.core.network.PacketId;
import forestry.core.network.PacketString;
import forestry.core.proxy.Proxies;

public class ContainerSolderingIron extends ContainerItemInventory<SolderingInventory> implements IGuiSelectable {

	public ContainerSolderingIron(EntityPlayer player, SolderingInventory inventory) {
		super(inventory, player.inventory, 8, 123);

		// Input
		this.addSlotToContainer(new SlotFiltered(inventory, 0, 152, 12));

		// Output
		this.addSlotToContainer(new SlotOutput(inventory, 1, 152, 92));

		// Ingredients
		this.addSlotToContainer(new SlotFiltered(inventory, 2, 12, 32));
		this.addSlotToContainer(new SlotFiltered(inventory, 3, 12, 52));
		this.addSlotToContainer(new SlotFiltered(inventory, 4, 12, 72));
		this.addSlotToContainer(new SlotFiltered(inventory, 5, 12, 92));
	}

	public ICircuitLayout getLayout() {
		return inventory.getLayout();
	}

	public static void advanceSelection(int index) {
		sendSelectionChange(index, 0);
	}

	public static void regressSelection(int index) {
		sendSelectionChange(index, 1);
	}

	private static void sendSelectionChange(int index, int advance) {
		PacketGuiSelect packet = new PacketGuiSelect(PacketId.GUI_SELECTION_CHANGE, index, advance);
		Proxies.net.sendToServer(packet);
	}

	@Override
	public void handleSelectionChange(EntityPlayer player, PacketGuiSelect packet) {

		if (packet.getSecondaryIndex() == 0) {
			if (packet.getPrimaryIndex() == 0) {
				inventory.advanceLayout();
			}
		} else if (packet.getPrimaryIndex() == 0) {
			inventory.regressLayout();
		}

		sendSelection(player);
	}

	public void sendSelection(EntityPlayer player) {
		PacketString packet = new PacketString(PacketId.GUI_LAYOUT_SELECT, inventory.getLayout().getUID());
		Proxies.net.sendToPlayer(packet, player);
	}

	@Override
	public void setSelection(PacketGuiSelect packet) {
	}

	public void setLayout(String layoutUID) {
		inventory.setLayout(layoutUID);
	}
}
