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

import net.minecraft.inventory.IInventory;

import forestry.apiculture.InventoryBeeHousing;
import forestry.apiculture.inventory.InventoryApiary;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.tiles.IFilterSlotDelegate;

public abstract class ContainerBeeHelper {
	public static <T extends IInventory & IFilterSlotDelegate> void addSlots(ContainerForestry container, T inventory, boolean hasFrames) {
		// Queen/Princess
		container.addSlotToContainer(new SlotFiltered(inventory, InventoryBeeHousing.SLOT_QUEEN, 29, 39));

		// Drone
		container.addSlotToContainer(new SlotFiltered(inventory, InventoryBeeHousing.SLOT_DRONE, 29, 65));

		// Frames
		if (hasFrames) {
			int slotFrames = InventoryApiary.SLOT_FRAMES_1;
			container.addSlotToContainer(new SlotFiltered(inventory, slotFrames++, 66, 23));
			container.addSlotToContainer(new SlotFiltered(inventory, slotFrames++, 66, 52));
			container.addSlotToContainer(new SlotFiltered(inventory, slotFrames, 66, 81));
		}

		// Product Inventory
		int slotProduct = InventoryBeeHousing.SLOT_PRODUCT_1;
		container.addSlotToContainer(new SlotOutput(inventory, slotProduct++, 116, 52));
		container.addSlotToContainer(new SlotOutput(inventory, slotProduct++, 137, 39));
		container.addSlotToContainer(new SlotOutput(inventory, slotProduct++, 137, 65));
		container.addSlotToContainer(new SlotOutput(inventory, slotProduct++, 116, 78));
		container.addSlotToContainer(new SlotOutput(inventory, slotProduct++, 95, 65));
		container.addSlotToContainer(new SlotOutput(inventory, slotProduct++, 95, 39));
		container.addSlotToContainer(new SlotOutput(inventory, slotProduct, 116, 26));
	}
}
