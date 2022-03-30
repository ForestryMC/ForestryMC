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
package forestry.core.gui;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import forestry.core.gui.slots.SlotLocked;
import forestry.core.inventory.ItemInventory;

public abstract class ContainerItemInventory<I extends ItemInventory> extends ContainerForestry {

	protected final I inventory;

	protected ContainerItemInventory(int windowId, I inventory, Inventory playerInventory, int xInv, int yInv, MenuType<?> type) {
		super(windowId, type);
		this.inventory = inventory;

		addPlayerInventory(playerInventory, xInv, yInv);
	}

	@Override
	protected void addHotbarSlot(Inventory playerInventory, int slot, int x, int y) {
		ItemStack stackInSlot = playerInventory.getItem(slot);

		if (inventory.isParentItemInventory(stackInSlot)) {
			addSlot(new SlotLocked(playerInventory, slot, x, y));
		} else {
			addSlot(new Slot(playerInventory, slot, x, y));
		}
	}

	@Override
	protected final boolean canAccess(Player player) {
		return stillValid(player);
	}

	@Override
	public final boolean stillValid(Player PlayerEntity) {
		return inventory.stillValid(PlayerEntity);
	}

	@Override
	public void clicked(int slotId, int dragType_or_button, ClickType clickTypeIn, Player player) {
		super.clicked(slotId, dragType_or_button, clickTypeIn, player);

		if (slotId > 0) {
			inventory.onSlotClick(slots.get(slotId).getSlotIndex(), player);
		}
	}

	public I getItemInventory() {
		return inventory;
	}

}
