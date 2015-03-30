package forestry.core.utils;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import forestry.core.gui.slots.SlotForestry;

public class SlotHelper {

	public static ItemStack slotClickPhantom(Slot slot, int mouseButton, int modifier, EntityPlayer player) {
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
				adjustPhantomSlot(slot, mouseButton, modifier);
			} else if (slot.isItemValid(stackHeld)) {
				if (StackUtils.isIdenticalItem(stackSlot, stackHeld)) {
					adjustPhantomSlot(slot, mouseButton, modifier);
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
		ItemStack originalStack = null;
		Slot slot = (Slot) inventorySlots.get(slotIndex);
		int numSlots = inventorySlots.size();
		if (slot != null && slot.getHasStack()) {
			ItemStack stackInSlot = slot.getStack();
			originalStack = stackInSlot.copy();

			if (slotIndex >= numSlots - 9 * 4 && tryShiftItem(inventorySlots, stackInSlot, numSlots)) {
				// NOOP
			} else if (slotIndex >= numSlots - 9 * 4 && slotIndex < numSlots - 9) {
				if (!shiftItemStack(inventorySlots, stackInSlot, numSlots - 9, numSlots)) {
					return null;
				}
			} else if (slotIndex >= numSlots - 9 && slotIndex < numSlots) {
				if (!shiftItemStack(inventorySlots, stackInSlot, numSlots - 9 * 4, numSlots - 9)) {
					return null;
				}
			} else if (!shiftItemStack(inventorySlots, stackInSlot, numSlots - 9 * 4, numSlots)) {
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
		}
		return originalStack;
	}

	private static void adjustPhantomSlot(Slot slot, int mouseButton, int modifier) {
		if (!((SlotForestry) slot).canAdjustPhantom()) {
			return;
		}
		ItemStack stackSlot = slot.getStack();
		int stackSize;
		if (modifier == 1) {
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

	private static void fillPhantomSlot(Slot slot, ItemStack stackHeld, int mouseButton) {
		if (!((SlotForestry) slot).canAdjustPhantom()) {
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

	private static boolean shiftItemStack(List inventorySlots, ItemStack stackToShift, int start, int end) {
		boolean changed = false;
		if (stackToShift.isStackable()) {
			for (int slotIndex = start; stackToShift.stackSize > 0 && slotIndex < end; slotIndex++) {
				Slot slot = (Slot) inventorySlots.get(slotIndex);
				ItemStack stackInSlot = slot.getStack();
				if (stackInSlot != null && StackUtils.isIdenticalItem(stackInSlot, stackToShift)) {
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
		}
		if (stackToShift.stackSize > 0) {
			for (int slotIndex = start; stackToShift.stackSize > 0 && slotIndex < end; slotIndex++) {
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
		}
		return changed;
	}

	private static boolean tryShiftItem(List inventorySlots, ItemStack stackToShift, int numSlots) {
		boolean success = tryShiftItem(inventorySlots, stackToShift, numSlots, true);
		if (stackToShift.stackSize > 0) {
			success |= tryShiftItem(inventorySlots, stackToShift, numSlots, false);
		}
		return success;
	}

	// if mergeOnly = true, don't shift into empty slots.
	private static boolean tryShiftItem(List inventorySlots, ItemStack stackToShift, int numSlots, boolean mergeOnly) {
		for (int machineIndex = 0; machineIndex < numSlots - 9 * 4; machineIndex++) {
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
			if (shiftItemStack(inventorySlots, stackToShift, machineIndex, machineIndex + 1)) {
				return true;
			}
		}
		return false;
	}
}
