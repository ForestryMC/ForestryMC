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
package forestry.core.utils;

import java.util.List;

import forestry.core.gui.slots.SlotCrafter;
import forestry.core.gui.slots.SlotForestry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

public abstract class SlotUtil {

	public static boolean isSlotInRange(int slotIndex, int start, int count) {
		return slotIndex >= start && slotIndex < start + count;
	}

	public static ItemStack slotClickPhantom(SlotForestry slot, int mouseButton, ClickType clickTypeIn, EntityPlayer player) {
		ItemStack stack = null;

		ItemStack stackSlot = slot.getStack();
		if (stackSlot != null) {
			stack = stackSlot.copy();
		}

		if (mouseButton == 2) {
			fillPhantomSlot(slot, null, mouseButton);
		} else if (mouseButton == 0 || mouseButton == 1) {
			InventoryPlayer playerInv = player.inventory;

			ItemStack stackHeld = playerInv.getItemStack();

			if (stackSlot == null) {
				if (stackHeld != null && slot.isItemValid(stackHeld)) {
					fillPhantomSlot(slot, stackHeld, mouseButton);
				}
			} else if (stackHeld == null) {
				adjustPhantomSlot(slot, mouseButton, clickTypeIn);
			} else if (slot.isItemValid(stackHeld)) {
				if (ItemStackUtil.isIdenticalItem(stackSlot, stackHeld)) {
					adjustPhantomSlot(slot, mouseButton, clickTypeIn);
				} else {
					fillPhantomSlot(slot, stackHeld, mouseButton);
				}
			}
		} else if (mouseButton == 5) {
			InventoryPlayer playerInv = player.inventory;
			ItemStack stackHeld = playerInv.getItemStack();
			if (!slot.getHasStack()) {
				fillPhantomSlot(slot, stackHeld, mouseButton);
			}
		}
		return stack;
	}

	public static ItemStack transferStackInSlot(List inventorySlots, EntityPlayer player, int slotIndex) {
		Slot slot = (Slot) inventorySlots.get(slotIndex);
		if (slot == null || !slot.getHasStack()) {
			return null;
		}

		boolean fromCraftingSlot = slot instanceof SlotCrafting || slot instanceof SlotCrafter;

		int numSlots = inventorySlots.size();
		ItemStack stackInSlot = slot.getStack();
		ItemStack originalStack = stackInSlot.copy();

		if (!shiftItemStack(inventorySlots, stackInSlot, slotIndex, numSlots, fromCraftingSlot)) {
			return null;
		}

		slot.onSlotChange(stackInSlot, originalStack);
		if (stackInSlot.stackSize <= 0) {
			slot.putStack(null);
		} else {
			slot.onSlotChanged();
		}

		if (stackInSlot.stackSize == originalStack.stackSize) {
			return null;
		}

		slot.onPickupFromSlot(player, stackInSlot);
		return originalStack;
	}

	private static boolean shiftItemStack(List inventorySlots, ItemStack stackInSlot, int slotIndex, int numSlots, boolean fromCraftingSlot) {
		if (isInPlayerInventory(slotIndex)) {
			if (shiftToMachineInventory(inventorySlots, stackInSlot, numSlots)) {
				return true;
			}

			if (isInPlayerHotbar(slotIndex)) {
				return shiftToPlayerInventoryNoHotbar(inventorySlots, stackInSlot);
			} else {
				return shiftToHotbar(inventorySlots, stackInSlot);
			}
		} else {
			if (fromCraftingSlot) {
				if (shiftToMachineInventory(inventorySlots, stackInSlot, numSlots)) {
					return true;
				}
			}
			return shiftToPlayerInventory(inventorySlots, stackInSlot);
		}
	}

	private static void adjustPhantomSlot(SlotForestry slot, int mouseButton, ClickType clickTypeIn) {
		if (!slot.canAdjustPhantom()) {
			return;
		}
		ItemStack stackSlot = slot.getStack();
		int stackSize;
		if (clickTypeIn == ClickType.QUICK_MOVE) {
			stackSize = mouseButton == 0 ? (stackSlot.stackSize + 1) / 2 : stackSlot.stackSize * 2;
		} else {
			stackSize = mouseButton == 0 ? stackSlot.stackSize - 1 : stackSlot.stackSize + 1;
		}

		if (stackSize > slot.getSlotStackLimit()) {
			stackSize = slot.getSlotStackLimit();
		}

		stackSlot.stackSize = stackSize;

		if (stackSlot.stackSize <= 0) {
			stackSlot = null;
		}

		slot.putStack(stackSlot);
	}

	private static void fillPhantomSlot(SlotForestry slot, ItemStack stackHeld, int mouseButton) {
		if (!slot.canAdjustPhantom()) {
			return;
		}

		if (stackHeld == null) {
			slot.putStack(null);
			return;
		}

		int stackSize = mouseButton == 0 ? stackHeld.stackSize : 1;
		if (stackSize > slot.getSlotStackLimit()) {
			stackSize = slot.getSlotStackLimit();
		}
		ItemStack phantomStack = stackHeld.copy();
		phantomStack.stackSize = stackSize;

		slot.putStack(phantomStack);
	}

	private static boolean shiftItemStackToRange(List inventorySlots, ItemStack stackToShift, int start, int count) {
		boolean changed = shiftItemStackToRangeMerge(inventorySlots, stackToShift, start, count);
		changed |= shiftItemStackToRangeOpenSlots(inventorySlots, stackToShift, start, count);
		return changed;
	}

	private static boolean shiftItemStackToRangeMerge(List inventorySlots, ItemStack stackToShift, int start, int count) {
		if (stackToShift == null || !stackToShift.isStackable() || stackToShift.stackSize <= 0) {
			return false;
		}

		boolean changed = false;
		for (int slotIndex = start; stackToShift.stackSize > 0 && slotIndex < start + count; slotIndex++) {
			Slot slot = (Slot) inventorySlots.get(slotIndex);
			ItemStack stackInSlot = slot.getStack();
			if (stackInSlot != null && ItemStackUtil.isIdenticalItem(stackInSlot, stackToShift)) {
				int resultingStackSize = stackInSlot.stackSize + stackToShift.stackSize;
				int max = Math.min(stackToShift.getMaxStackSize(), slot.getSlotStackLimit());
				if (resultingStackSize <= max) {
					stackToShift.stackSize = 0;
					stackInSlot.stackSize = resultingStackSize;
					slot.onSlotChanged();
					changed = true;
				} else if (stackInSlot.stackSize < max) {
					stackToShift.stackSize -= max - stackInSlot.stackSize;
					stackInSlot.stackSize = max;
					slot.onSlotChanged();
					changed = true;
				}
			}
		}
		return changed;
	}

	private static boolean shiftItemStackToRangeOpenSlots(List inventorySlots, ItemStack stackToShift, int start, int count) {
		if (stackToShift == null || stackToShift.stackSize <= 0) {
			return false;
		}

		boolean changed = false;
		for (int slotIndex = start; stackToShift.stackSize > 0 && slotIndex < start + count; slotIndex++) {
			Slot slot = (Slot) inventorySlots.get(slotIndex);
			ItemStack stackInSlot = slot.getStack();
			if (stackInSlot == null) {
				int max = Math.min(stackToShift.getMaxStackSize(), slot.getSlotStackLimit());
				stackInSlot = stackToShift.copy();
				stackInSlot.stackSize = Math.min(stackToShift.stackSize, max);
				stackToShift.stackSize -= stackInSlot.stackSize;
				slot.putStack(stackInSlot);
				slot.onSlotChanged();
				changed = true;
			}
		}
		return changed;
	}

	private static final int playerInventorySize = 9 * 4;
	private static final int playerHotbarSize = 9;

	private static boolean isInPlayerInventory(int slotIndex) {
		return slotIndex < playerInventorySize;
	}

	private static boolean isInPlayerHotbar(int slotIndex) {
		return SlotUtil.isSlotInRange(slotIndex, playerInventorySize - playerHotbarSize, playerInventorySize);
	}

	private static boolean shiftToPlayerInventory(List inventorySlots, ItemStack stackInSlot) {
		int playerHotbarStart = playerInventorySize - playerHotbarSize;

		// try to merge with existing stacks, hotbar first
		boolean shifted = shiftItemStackToRangeMerge(inventorySlots, stackInSlot, playerHotbarStart, playerHotbarSize);
		shifted |= shiftItemStackToRangeMerge(inventorySlots, stackInSlot, 0, playerHotbarStart);

		// shift to open slots, hotbar first
		shifted |= shiftItemStackToRangeOpenSlots(inventorySlots, stackInSlot, playerHotbarStart, playerHotbarSize);
		shifted |= shiftItemStackToRangeOpenSlots(inventorySlots, stackInSlot, 0, playerHotbarStart);
		return shifted;
	}

	private static boolean shiftToPlayerInventoryNoHotbar(List inventorySlots, ItemStack stackInSlot) {
		int playerHotbarStart = playerInventorySize - playerHotbarSize;
		return shiftItemStackToRange(inventorySlots, stackInSlot, 0, playerHotbarStart);
	}

	private static boolean shiftToHotbar(List inventorySlots, ItemStack stackInSlot) {
		int playerHotbarStart = playerInventorySize - playerHotbarSize;
		return shiftItemStackToRange(inventorySlots, stackInSlot, playerHotbarStart, playerHotbarSize);
	}

	private static boolean shiftToMachineInventory(List inventorySlots, ItemStack stackToShift, int numSlots) {
		boolean success = false;
		if (stackToShift.isStackable()) {
			success = shiftToMachineInventory(inventorySlots, stackToShift, numSlots, true);
		}
		if (stackToShift.stackSize > 0) {
			success |= shiftToMachineInventory(inventorySlots, stackToShift, numSlots, false);
		}
		return success;
	}

	// if mergeOnly = true, don't shift into empty slots.
	private static boolean shiftToMachineInventory(List inventorySlots, ItemStack stackToShift, int numSlots, boolean mergeOnly) {
		for (int machineIndex = playerInventorySize; machineIndex < numSlots; machineIndex++) {
			Slot slot = (Slot) inventorySlots.get(machineIndex);
			if (mergeOnly && slot.getStack() == null) {
				continue;
			}
			if (slot instanceof SlotForestry) {
				SlotForestry slotForestry = (SlotForestry) slot;
				if (!slotForestry.canShift() || slotForestry.isPhantom()) {
					continue;
				}
			}
			if (!slot.isItemValid(stackToShift)) {
				continue;
			}
			if (shiftItemStackToRange(inventorySlots, stackToShift, machineIndex, 1)) {
				return true;
			}
		}
		return false;
	}
}
