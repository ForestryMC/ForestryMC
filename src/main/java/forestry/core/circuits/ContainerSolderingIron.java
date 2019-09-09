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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

	public static ContainerSolderingIron fromNetwork(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
		Hand hand = extraData.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND;
		PlayerEntity player = playerInv.player;
		ItemInventorySolderingIron inv = new ItemInventorySolderingIron(player, player.getHeldItem(hand));
		return new ContainerSolderingIron(windowId, player, inv);
	}

	public ContainerSolderingIron(int windowId, PlayerEntity player, ItemInventorySolderingIron inventory) {
		super(windowId, inventory, player.inventory, 8, 123, null);

		// Input
		this.addSlot(new SlotFiltered(inventory, 0, 152, 12));

		// Output
		this.addSlot(new SlotOutput(inventory, 1, 152, 92));

		// Ingredients
		this.addSlot(new SlotFiltered(inventory, 2, 12, 32));
		this.addSlot(new SlotFiltered(inventory, 3, 12, 52));
		this.addSlot(new SlotFiltered(inventory, 4, 12, 72));
		this.addSlot(new SlotFiltered(inventory, 5, 12, 92));
	}

	public ICircuitLayout getLayout() {
		return inventory.getLayout();
	}

	@OnlyIn(Dist.CLIENT)
	public static void advanceSelection(int index) {
		sendSelectionChange(index, 0);
	}

	@OnlyIn(Dist.CLIENT)
	public static void regressSelection(int index) {
		sendSelectionChange(index, 1);
	}

	@OnlyIn(Dist.CLIENT)
	private static void sendSelectionChange(int index, int advance) {
		IForestryPacketServer packet = new PacketGuiSelectRequest(index, advance);
		NetworkUtil.sendToServer(packet);
	}

	@Override
	public void handleSelectionRequest(ServerPlayerEntity player, int primary, int secondary) {

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
