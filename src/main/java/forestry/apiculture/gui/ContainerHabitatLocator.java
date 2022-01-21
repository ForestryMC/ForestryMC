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

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;

import forestry.apiculture.features.ApicultureContainers;
import forestry.apiculture.inventory.ItemInventoryHabitatLocator;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;

public class ContainerHabitatLocator extends ContainerItemInventory<ItemInventoryHabitatLocator> {

	public static ContainerHabitatLocator fromNetwork(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
		InteractionHand hand = extraData.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		Player player = playerInv.player;
		ItemInventoryHabitatLocator inv = new ItemInventoryHabitatLocator(player, player.getItemInHand(hand));
		return new ContainerHabitatLocator(windowId, player, inv);
	}

	public ContainerHabitatLocator(int windowId, Player player, ItemInventoryHabitatLocator inventory) {
		super(windowId, inventory, player.inventory, 8, 102, ApicultureContainers.HABITAT_LOCATOR.containerType());

		// Energy
		this.addSlot(new SlotFiltered(inventory, 2, 152, 8));

		// Bee to analyze
		this.addSlot(new SlotFiltered(inventory, 0, 152, 32));
		// Analyzed bee
		this.addSlot(new SlotOutput(inventory, 1, 152, 75));
	}
}
