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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimaps;

import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import forestry.core.gui.slots.SlotFilteredInventory;
import forestry.core.gui.slots.SlotForestry;
import forestry.core.gui.slots.SlotLocked;
import forestry.core.network.IForestryPacketClient;
import forestry.core.proxy.Proxies;
import forestry.core.utils.SlotUtil;

import invtweaks.api.container.ContainerSection;
import invtweaks.api.container.ContainerSectionCallback;

@invtweaks.api.container.ChestContainer(showButtons = false)
public abstract class ContainerForestry extends Container {
	protected final void addPlayerInventory(InventoryPlayer playerInventory, int xInv, int yInv) {
		// Player inventory
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				addSlotToContainer(new Slot(playerInventory, column + row * 9 + 9, xInv + column * 18, yInv + row * 18));
			}
		}
		// Player hotbar
		for (int column = 0; column < 9; column++) {
			addHotbarSlot(playerInventory, column, xInv + column * 18, yInv + 58);
		}
	}

	protected void addHotbarSlot(InventoryPlayer playerInventory, int slot, int x, int y) {
		addSlotToContainer(new Slot(playerInventory, slot, x, y));
	}

	@Override
	public Slot addSlotToContainer(Slot p_75146_1_) {
		return super.addSlotToContainer(p_75146_1_);
	}

	@Override
	public ItemStack slotClick(int slotIndex, int button, int modifier, EntityPlayer player) {
		if (!canAccess(player)) {
			return null;
		}

		if (modifier == 2 && button >= 0 && button < 9) {
			// hotkey used to move item from slot to hotbar
			int hotbarSlotIndex = 27 + button;
			Slot hotbarSlot = getSlot(hotbarSlotIndex);
			if (hotbarSlot instanceof SlotLocked) {
				return null;
			}
		}

		Slot slot = (slotIndex < 0) ? null : getSlot(slotIndex);
		if (slot instanceof SlotForestry) {
			SlotForestry slotForestry = (SlotForestry) slot;
			if (slotForestry.isPhantom()) {
				return SlotUtil.slotClickPhantom(slotForestry, button, modifier, player);
			}
		}

		return super.slotClick(slotIndex, button, modifier, player);
	}

	@Override
	public final ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		if (!canAccess(player)) {
			return null;
		}

		return SlotUtil.transferStackInSlot(inventorySlots, player, slotIndex);
	}

	protected abstract boolean canAccess(EntityPlayer player);

	protected final void sendPacketToCrafters(IForestryPacketClient packet) {
		for (Object crafter : crafters) {
			if (crafter instanceof EntityPlayer) {
				Proxies.net.sendToPlayer(packet, (EntityPlayer) crafter);
			}
		}
	}

	@SuppressWarnings("unused") // inventory tweaks
	@ContainerSectionCallback
	public Map<ContainerSection, List<Slot>> getContainerSections() {
		ArrayListMultimap<ContainerSection, Slot> map = ArrayListMultimap.create();

		for (Object object : inventorySlots) {
			if (!(object instanceof Slot)) {
				continue;
			}
			Slot slot = (Slot) object;

			if (slot.inventory instanceof InventoryPlayer) {
				map.put(ContainerSection.INVENTORY, slot);
				if (slot.slotNumber < 9) {
					map.put(ContainerSection.INVENTORY_HOTBAR, slot);
				} else if (slot.slotNumber < 36) {
					map.put(ContainerSection.INVENTORY_NOT_HOTBAR, slot);
				} else {
					map.put(ContainerSection.ARMOR, slot);
				}
			} else {
				if (!(slot instanceof SlotForestry) || slot instanceof SlotFilteredInventory) {
					map.put(ContainerSection.CHEST, slot);
				}
			}
		}
		
		return Multimaps.asMap(map);
	}
}
