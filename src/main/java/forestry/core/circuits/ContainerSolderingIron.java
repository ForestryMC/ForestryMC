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
import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.circuits.ICircuitLayout;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.IGuiSelectable;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.inventory.ItemInventorySolderingIron;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.packets.PacketGuiLayoutSelect;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.utils.NetworkUtil;

public class ContainerSolderingIron extends ContainerItemInventory<ItemInventorySolderingIron> implements IGuiSelectable {

	public ContainerSolderingIron(EntityPlayer player, ItemInventorySolderingIron inventory) {
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

	@SideOnly(Side.CLIENT)
	public static void advanceSelection(int index) {
		sendSelectionChange(index, 0);
	}

	@SideOnly(Side.CLIENT)
	public static void regressSelection(int index) {
		sendSelectionChange(index, 1);
	}

	@SideOnly(Side.CLIENT)
	private static void sendSelectionChange(int index, int advance) {
		IForestryPacketServer packet = new PacketGuiSelectRequest(index, advance);
		NetworkUtil.sendToServer(packet);
	}

	@Override
	public void handleSelectionRequest(EntityPlayerMP player, int primary, int secondary) {

		if (secondary == 0) {
			if (primary == 0) {
				inventory.advanceLayout();
			}
		} else if (primary == 0) {
			inventory.regressLayout();
		}

		IForestryPacketClient packetResponse = new PacketGuiLayoutSelect(inventory.getLayout().getUID());
		NetworkUtil.sendToPlayer(packetResponse, player);
	}

	public void setLayout(ICircuitLayout layout) {
		inventory.setLayout(layout);
	}
}
