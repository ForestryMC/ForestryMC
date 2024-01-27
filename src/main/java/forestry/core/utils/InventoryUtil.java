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

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandler;

import forestry.core.circuits.ISocketable;
import forestry.core.inventory.ItemHandlerInventoryManipulator;
import forestry.core.inventory.StandardStackFilters;

public abstract class InventoryUtil {
	/**
	 * Attempts to move an ItemStack from one inventory to another.
	 *
	 * @param source The source IInventory.
	 * @param dest   The destination IInventory.
	 * @return true if any items were moved
	 */
	public static boolean moveItemStack(IItemHandler source, IItemHandler dest) {
		ItemHandlerInventoryManipulator manipulator = new ItemHandlerInventoryManipulator(source);
		return manipulator.transferOneStack(dest, StandardStackFilters.ALL);
	}

	/**
	 * Attempts to move an ItemStack from one inventory to another.
	 *
	 * @param source       The source IInventory.
	 * @param destinations The destination IInventory.
	 * @return true if any items were moved
	 */
	public static boolean moveItemStack(IItemHandler source, Iterable<IItemHandler> destinations) {
		for (IItemHandler dest : destinations) {
			if (moveItemStack(source, dest)) {
				return true;
			}
		}
		return false;
	}

	/* REMOVAL */


	public static boolean consumeIngredients(Container inventory, NonNullList<Ingredient> ingredients, @Nullable Player player, boolean stowContainer, boolean craftingTools, boolean doRemove) {
		int[] consumeStacks = ItemStackUtil.createConsume(ingredients, inventory, craftingTools);
		if (doRemove && consumeStacks.length > 0) {
			return consumeItems(inventory, consumeStacks, player, stowContainer);
		} else {
			return consumeStacks.length > 0;
		}
	}

	private static boolean consumeItems(Container inventory, int[] consumeStacks, @Nullable Player player, boolean stowContainer) {
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			int count = consumeStacks[i];
			if (count <= 0) {
				continue;
			}
			ItemStack oldStack = inventory.getItem(i);
			ItemStack removed = inventory.removeItem(i, count);

			if (stowContainer && oldStack.getItem().hasCraftingRemainingItem(oldStack)) {
				stowContainerItem(removed, inventory, i, player);
			}

			if (count > removed.getCount()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Removes a set of items from an inventory.
	 * Removes the exact items first if they exist, and then removes crafting equivalents.
	 * If the inventory doesn't have all the required items, returns false without removing anything.
	 * If stowContainer is true, items with containers will have their container stowed.
	 */
	public static boolean removeSets(Container inventory, int count, NonNullList<ItemStack> set, @Nullable Player player, boolean stowContainer, boolean craftingTools, boolean doRemove) {
		NonNullList<ItemStack> stock = getStacks(inventory);

		if (doRemove) {
			NonNullList<ItemStack> removed = removeSets(inventory, count, set, player, stowContainer, craftingTools);
			return removed != null && removed.size() >= count;
		} else {
			return ItemStackUtil.containsSets(set, stock, craftingTools) >= count;
		}
	}

	@Nullable
	public static NonNullList<ItemStack> removeSets(Container inventory, int count, NonNullList<ItemStack> set, @Nullable Player player, boolean stowContainer, boolean craftingTools) {
		NonNullList<ItemStack> removed = NonNullList.withSize(set.size(), ItemStack.EMPTY);
		NonNullList<ItemStack> stock = getStacks(inventory);

		if (ItemStackUtil.containsSets(set, stock, craftingTools) < count) {
			return null;
		}

		for (int i = 0; i < set.size(); i++) {
			ItemStack itemStack = set.get(i);
			if (!itemStack.isEmpty()) {
				ItemStack stackToRemove = itemStack.copy();
				stackToRemove.setCount(stackToRemove.getCount() * count);

				// try to remove the exact stack first
				ItemStack removedStack = removeStack(inventory, stackToRemove, player, stowContainer, false);
				if (removedStack.isEmpty()) {
					// remove crafting equivalents next
					removedStack = removeStack(inventory, stackToRemove, player, stowContainer, craftingTools);
				}

				removed.set(i, removedStack);
			}
		}
		return removed;
	}

	/**
	 * Private Helper for removeSetsFromInventory. Assumes removal is possible.
	 */
	private static ItemStack removeStack(Container inventory, ItemStack stackToRemove, @Nullable Player player, boolean stowContainer, boolean craftingTools) {
		for (int j = 0; j < inventory.getContainerSize(); j++) {
			ItemStack stackInSlot = inventory.getItem(j);
			if (!stackInSlot.isEmpty()) {
				if (ItemStackUtil.isCraftingEquivalent(stackToRemove, stackInSlot, craftingTools)) {
					ItemStack removed = inventory.removeItem(j, stackToRemove.getCount());
					stackToRemove.shrink(removed.getCount());

					if (stowContainer && stackToRemove.getItem().hasCraftingRemainingItem(stackToRemove)) {
						stowContainerItem(removed, inventory, j, player);
					}

					if (stackToRemove.isEmpty()) {
						return removed;
					}
				}
			}
		}
		return ItemStack.EMPTY;
	}

	/* CONTAINS */

	public static boolean contains(Container inventory, NonNullList<ItemStack> query) {
		return contains(inventory, query, 0, inventory.getContainerSize());
	}

	public static boolean contains(Container inventory, NonNullList<ItemStack> query, int startSlot, int slots) {
		NonNullList<ItemStack> stock = getStacks(inventory, startSlot, slots);
		return ItemStackUtil.containsSets(query, stock) > 0;
	}

	public static boolean isEmpty(Container inventory, int slotStart, int slotCount) {
		for (int i = slotStart; i < slotStart + slotCount; i++) {
			if (!inventory.getItem(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public static NonNullList<ItemStack> getStacks(Container inventory) {
		NonNullList<ItemStack> stacks = NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			stacks.set(i, inventory.getItem(i));
		}
		return stacks;
	}

	public static NonNullList<ItemStack> getStacks(Container inventory, int slot1, int length) {
		NonNullList<ItemStack> result = NonNullList.withSize(length, ItemStack.EMPTY);
		for (int i = slot1; i < slot1 + length; i++) {
			result.set(i - slot1, inventory.getItem(i));
		}
		return result;
	}

	public static boolean tryAddStacksCopy(Container inventory, NonNullList<ItemStack> stacks, int startSlot, int slots, boolean all) {

		for (ItemStack stack : stacks) {
			if (stack == null || stack.isEmpty()) {
				continue;
			}

			if (!tryAddStack(inventory, stack.copy(), startSlot, slots, all)) {
				return false;
			}
		}

		return true;
	}

	public static boolean tryAddStack(Container inventory, ItemStack stack, boolean all) {
		return tryAddStack(inventory, stack, 0, inventory.getContainerSize(), all, true);
	}

	public static boolean tryAddStack(Container inventory, ItemStack stack, boolean all, boolean doAdd) {
		return tryAddStack(inventory, stack, 0, inventory.getContainerSize(), all, doAdd);
	}

	/**
	 * Tries to add a stack to the specified slot range.
	 */
	public static boolean tryAddStack(Container inventory, ItemStack stack, int startSlot, int slots, boolean all) {
		return tryAddStack(inventory, stack, startSlot, slots, all, true);
	}

	public static boolean tryAddStack(Container inventory, ItemStack stack, int startSlot, int slots, boolean all, boolean doAdd) {
		int added = addStack(inventory, stack, startSlot, slots, false);
		boolean success = all ? added == stack.getCount() : added > 0;

		if (success && doAdd) {
			addStack(inventory, stack, startSlot, slots, true);
		}

		return success;
	}

	public static int addStack(Container inventory, ItemStack stack, boolean doAdd) {
		return addStack(inventory, stack, 0, inventory.getContainerSize(), doAdd);
	}

	public static int addStack(Container inventory, ItemStack stack, int startSlot, int slots, boolean doAdd) {
		if (stack.isEmpty()) {
			return 0;
		}

		int added = 0;
		// Add to existing stacks first
		for (int i = startSlot; i < startSlot + slots; i++) {

			ItemStack inventoryStack = inventory.getItem(i);
			// Empty slot. Add
			if (inventoryStack.isEmpty()) {
				continue;
			}

			// Already occupied by different item, skip this slot.
			if (!inventoryStack.isStackable()) {
				continue;
			}
			if (!inventoryStack.sameItem(stack)) {
				continue;
			}
			if (!ItemStack.tagMatches(inventoryStack, stack)) {
				continue;
			}

			int remain = stack.getCount() - added;
			int space = inventoryStack.getMaxStackSize() - inventoryStack.getCount();
			// No space left, skip this slot.
			if (space <= 0) {
				continue;
			}
			// Enough space
			if (space >= remain) {
				if (doAdd) {
					inventoryStack.grow(remain);
				}
				return stack.getCount();
			}

			// Not enough space
			if (doAdd) {
				inventoryStack.setCount(inventoryStack.getMaxStackSize());
			}

			added += space;
		}

		if (added >= stack.getCount()) {
			return added;
		}

		for (int i = startSlot; i < startSlot + slots; i++) {
			if (inventory.getItem(i).isEmpty()) {
				if (doAdd) {
					inventory.setItem(i, stack.copy());
					inventory.getItem(i).setCount(stack.getCount() - added);
				}
				return stack.getCount();
			}
		}

		return added;
	}

	public static boolean stowInInventory(ItemStack itemstack, Container inventory, boolean doAdd) {
		return stowInInventory(itemstack, inventory, doAdd, 0, inventory.getContainerSize());
	}

	public static boolean stowInInventory(ItemStack itemstack, Container inventory, boolean doAdd, int slot1, int count) {

		boolean added = false;

		for (int i = slot1; i < slot1 + count; i++) {
			ItemStack inventoryStack = inventory.getItem(i);

			// Grab those free slots
			if (inventoryStack.isEmpty()) {
				if (doAdd) {
					inventory.setItem(i, itemstack.copy());
					itemstack.setCount(0);
				}
				return true;
			}

			// Already full
			if (inventoryStack.getCount() >= inventoryStack.getMaxStackSize()) {
				continue;
			}

			// Not same type
			if (!inventoryStack.sameItem(itemstack)) {
				continue;
			}
			if (!ItemStack.tagMatches(inventoryStack, itemstack)) {
				continue;
			}

			int space = inventoryStack.getMaxStackSize() - inventoryStack.getCount();

			// Enough space to add all
			if (space > itemstack.getCount()) {
				if (doAdd) {
					inventoryStack.grow(itemstack.getCount());
					itemstack.setCount(0);
				}
				return true;
				// Only part can be added
			} else {
				if (doAdd) {
					inventoryStack.setCount(inventoryStack.getMaxStackSize());
					itemstack.shrink(space);
				}
				added = true;
			}

		}

		return added;
	}

	public static void stowContainerItem(ItemStack itemstack, Container stowing, int slotIndex, @Nullable Player player) {
		if (!itemstack.getItem().hasCraftingRemainingItem(itemstack)) {
			return;
		}

		ItemStack container = ForgeHooks.getCraftingRemainingItem(itemstack);
		if (!container.isEmpty()) {
			if (!tryAddStack(stowing, container, slotIndex, 1, true)) {
				if (!tryAddStack(stowing, container, true) && player != null) {
					player.drop(container, true);
				}
			}
		}
	}

	public static void deepCopyInventoryContents(Container source, Container destination) {
		if (source.getContainerSize() != destination.getContainerSize()) {
			throw new IllegalArgumentException("Inventory sizes do not match. Source: " + source + ", Destination: " + destination);
		}

		for (int i = 0; i < source.getContainerSize(); i++) {
			ItemStack stack = source.getItem(i);
			if (!stack.isEmpty()) {
				stack = stack.copy();
			}
			destination.setItem(i, stack);
		}
	}

	public static void dropSockets(ISocketable socketable, Level world, double x, double y, double z) {
		for (int slot = 0; slot < socketable.getSocketCount(); slot++) {
			ItemStack itemstack = socketable.getSocket(slot);
			Containers.dropItemStack(world, x, y, z, itemstack);
			socketable.setSocket(slot, ItemStack.EMPTY);
		}
	}

	public static void dropSockets(ISocketable socketable, Level world, BlockPos pos) {
		dropSockets(socketable, world, pos.getX(), pos.getY(), pos.getZ());
	}

	/* NBT */

	/**
	 * The database has an inventory large enough that int must be used here instead of byte
	 */
	public static void readFromNBT(Container inventory, String name, CompoundTag compoundNBT) {
		if (!compoundNBT.contains(name)) {
			return;
		}

		ListTag nbttaglist = compoundNBT.getList(name, 10);

		for (int j = 0; j < nbttaglist.size(); ++j) {
			CompoundTag compoundNBT2 = nbttaglist.getCompound(j);
			int index = compoundNBT2.getInt("Slot");
			inventory.setItem(index, ItemStack.of(compoundNBT2));
		}
	}

	public static void writeToNBT(Container inventory, String name, CompoundTag compoundNBT) {
		ListTag nbttaglist = new ListTag();
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			if (!inventory.getItem(i).isEmpty()) {
				CompoundTag compoundNBT2 = new CompoundTag();
				compoundNBT2.putInt("Slot", i);
				inventory.getItem(i).save(compoundNBT2);
				nbttaglist.add(compoundNBT2);
			}
		}
		compoundNBT.put(name, nbttaglist);
	}
}
