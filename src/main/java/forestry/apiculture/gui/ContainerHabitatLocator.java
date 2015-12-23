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

import forestry.apiculture.inventory.ItemInventoryHabitatLocator;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;

public class ContainerHabitatLocator extends ContainerItemInventory<ItemInventoryHabitatLocator> {

	public ContainerHabitatLocator(EntityPlayer player, ItemInventoryHabitatLocator inventory) {
		super(inventory, player.inventory, 8, 102);

		// Energy
		this.addSlotToContainer(new SlotFiltered(inventory, 2, 152, 8));

		// Bee to analyze
		this.addSlotToContainer(new SlotFiltered(inventory, 0, 152, 32));
		// Analyzed bee
		this.addSlotToContainer(new SlotOutput(inventory, 1, 152, 75));
	}
}
