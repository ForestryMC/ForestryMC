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
package forestry.core.inventory.manipulators;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import forestry.core.inventory.filters.IStackFilter;
import forestry.core.inventory.iterators.IExtInvSlot;
import forestry.core.inventory.iterators.InventoryIterator;
import forestry.core.utils.InventoryUtil;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class StandardInventoryManipulator extends InventoryManipulator<IExtInvSlot> {

    private final IInventory inv;

    protected StandardInventoryManipulator(IInventory inv) {
        this.inv = inv;
    }

    @Override
    public Iterator<IExtInvSlot> iterator() {
        return InventoryIterator.getIterable(inv).iterator();
    }

    @Override
	protected ItemStack addStack(ItemStack stack, boolean doAdd) {
        if (stack == null || stack.stackSize <= 0) {
			return null;
		}
        stack = stack.copy();
        List<IExtInvSlot> filledSlots = new ArrayList<IExtInvSlot>(inv.getSizeInventory());
        List<IExtInvSlot> emptySlots = new ArrayList<IExtInvSlot>(inv.getSizeInventory());
        for (IExtInvSlot slot : this) {
            if (slot.canPutStackInSlot(stack)) {
				if (slot.getStackInSlot() == null) {
					emptySlots.add(slot);
				} else {
					filledSlots.add(slot);
				}
			}
        }

        int injected = 0;
        injected = tryPut(filledSlots, stack, injected, doAdd);
        injected = tryPut(emptySlots, stack, injected, doAdd);
        stack.stackSize -= injected;
        if (stack.stackSize <= 0) {
			return null;
		}
        return stack;
    }

    private int tryPut(List<IExtInvSlot> slots, ItemStack stack, int injected, boolean doAdd) {
        if (injected >= stack.stackSize) {
			return injected;
		}
        for (IExtInvSlot slot : slots) {
            ItemStack stackInSlot = slot.getStackInSlot();
            if (stackInSlot == null || InventoryUtil.isItemEqual(stackInSlot, stack)) {
                int used = addToSlot(slot, stack, stack.stackSize - injected, doAdd);
                if (used > 0) {
                    injected += used;
                    if (injected >= stack.stackSize) {
						return injected;
					}
                }
            }
        }
        return injected;
    }

    /**
     * @param available Amount we can move
     * @return Return the number of items moved.
     */
    private int addToSlot(IExtInvSlot slot, ItemStack stack, int available, boolean doAdd) {
        int max = Math.min(stack.getMaxStackSize(), inv.getInventoryStackLimit());

        ItemStack stackInSlot = slot.getStackInSlot();
        if (stackInSlot == null) {
            int wanted = Math.min(available, max);
            if (doAdd) {
                stackInSlot = stack.copy();
                stackInSlot.stackSize = wanted;
                slot.setStackInSlot(stackInSlot);
            }
            return wanted;
        }

        if (!InventoryUtil.isItemEqual(stack, stackInSlot)) {
			return 0;
		}

        int wanted = max - stackInSlot.stackSize;
        if (wanted <= 0) {
			return 0;
		}

        if (wanted > available) {
			wanted = available;
		}

        if (doAdd) {
            stackInSlot.stackSize += wanted;
            slot.setStackInSlot(stackInSlot);
        }
        return wanted;
    }

    @Override
    protected List<ItemStack> removeItem(IStackFilter filter, int maxAmount, boolean doRemove) {
        int amountNeeded = maxAmount;
        List<ItemStack> outputList = new ArrayList<ItemStack>();
        for (IExtInvSlot slot : this) {
            if (amountNeeded <= 0) {
				break;
			}
            ItemStack stack = slot.getStackInSlot();
            if (stack != null && stack.stackSize > 0 && slot.canTakeStackFromSlot(stack) && filter.apply(stack)) {
                ItemStack output = stack.copy();
                if (output.stackSize >= amountNeeded) {
                    output.stackSize = amountNeeded;
                    if (doRemove) {
                        stack.stackSize -= amountNeeded;
                        if (stack.stackSize <= 0) {
							stack = null;
						}
                        slot.setStackInSlot(stack);
                    }
                    amountNeeded = 0;
                    outputList.add(output);
                } else {
                    amountNeeded -= output.stackSize;
                    outputList.add(output);
                    if (doRemove) {
						slot.setStackInSlot(null);
					}
                }
            }
        }
        return outputList;
    }

}
