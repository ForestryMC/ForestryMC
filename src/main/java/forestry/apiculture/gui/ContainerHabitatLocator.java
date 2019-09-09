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
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;

import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.inventory.ItemInventoryHabitatLocator;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;

public class ContainerHabitatLocator extends ContainerItemInventory<ItemInventoryHabitatLocator> {

	public static ContainerHabitatLocator fromNetwork(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
		Hand hand = extraData.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND;
		PlayerEntity player = playerInv.player;
		ItemInventoryHabitatLocator inv = new ItemInventoryHabitatLocator(player, player.getHeldItem(hand));
		return new ContainerHabitatLocator(windowId, player, inv);
	}

	public ContainerHabitatLocator(int windowId, PlayerEntity player, ItemInventoryHabitatLocator inventory) {
		super(windowId, inventory, player.inventory, 8, 102, ModuleApiculture.getContainerTypes().HABITAT_LOCATOR);

		// Energy
		this.addSlot(new SlotFiltered(inventory, 2, 152, 8));

		// Bee to analyze
		this.addSlot(new SlotFiltered(inventory, 0, 152, 32));
		// Analyzed bee
		this.addSlot(new SlotOutput(inventory, 1, 152, 75));
	}
}
