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
import java.util.Collection;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.core.ForestryAPI;
import forestry.core.circuits.ISocketable;
import forestry.core.inventory.filters.StandardStackFilters;
import forestry.core.inventory.manipulators.ItemHandlerInventoryManipulator;
import forestry.core.tiles.AdjacentTileCache;
import forestry.plugins.ForestryPluginUids;

public abstract class InventoryUtil {

	public static boolean isWildcard(ItemStack stack) {
		return stack.getItemDamage() == OreDictionary.WILDCARD_VALUE;
	}

	/**
	 * A more robust item comparison function. Supports items with damage = -1
	 * matching any sub-type.
	 *
	 * @param a An ItemStack
	 * @param b An ItemStack
	 * @return True if equal
	 */
	public static boolean isItemEqual(ItemStack a, ItemStack b) {
		return isItemEqual(a, b, true, true);
	}

	public static boolean isItemEqual(final ItemStack a, final ItemStack b, final boolean matchDamage, final boolean matchNBT) {
		if (a == null || b == null) {
			return false;
		}
		if (a.getItem() != b.getItem()) {
			return false;
		}
		if (matchNBT && !ItemStack.areItemStackTagsEqual(a, b)) {
			return false;
		}
		if (matchDamage && a.getHasSubtypes()) {
			if (isWildcard(a) || isWildcard(b)) {
				return true;
			}
			if (a.getItemDamage() != b.getItemDamage()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns true if the item is equal to any one of several possible matches.
	 */
	public static boolean isItemEqual(ItemStack stack, ItemStack... matches) {
		for (ItemStack match : matches) {
			if (isItemEqual(stack, match)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the item is equal to any one of several possible matches.
	 *
	 * @param stack   the ItemStack to test
	 * @param matches the ItemStacks to test against
	 * @return true if a match is found
	 */
	public static boolean isItemEqual(ItemStack stack, Collection<ItemStack> matches) {
		for (ItemStack match : matches) {
			if (isItemEqual(stack, match)) {
				return true;
			}
		}
		return false;
	}

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

	/**
	 * Attempts to move a single item from the source inventory into a adjacent Buildcraft pipe.
	 * If the attempt fails, the source Inventory will not be modified.
	 *
	 * @param source    The source inventory
	 * @param tileCache The tile cache of the source block.
	 * @return true if an item was inserted, otherwise false.
	 */
	public static boolean moveOneItemToPipe(IItemHandler source, AdjacentTileCache tileCache) {
		return moveOneItemToPipe(source, tileCache, EnumFacing.values());
	}

	public static boolean moveOneItemToPipe(IItemHandler source, AdjacentTileCache tileCache, EnumFacing[] directions) {
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.BUILDCRAFT_TRANSPORT)) {
			return internal_moveOneItemToPipe(source, tileCache, directions);
		}

		return false;
	}

	//TODO Buildcraft for 1.9
	@Optional.Method(modid = "BuildCraftAPI|transport")
	private static boolean internal_moveOneItemToPipe(IItemHandler source, AdjacentTileCache tileCache, EnumFacing[] directions) {
//		IInventory invClone = new InventoryCopy(source);
//		ItemStack stackToMove = removeOneItem(invClone);
//		if (stackToMove == null) {
//			return false;
//		}
//		if (stackToMove.stackSize <= 0) {
//			return false;
//		}
//
//		List<Map.Entry<EnumFacing, IPipeTile>> pipes = new ArrayList<>();
//		boolean foundPipe = false;
//		for (EnumFacing side : directions) {
//			TileEntity tile = tileCache.getTileOnSide(side);
//			if (tile instanceof IPipeTile) {
//				IPipeTile pipe = (IPipeTile) tile;
//				if (pipe.getPipeType() == IPipeTile.PipeType.ITEM && pipe.isPipeConnected(side.getOpposite())) {
//					pipes.add(new AbstractMap.SimpleEntry<>(side, pipe));
//					foundPipe = true;
//				}
//			}
//		}
//
//		if (!foundPipe) {
//			return false;
//		}
//
//		int choice = tileCache.getSource().getWorld().rand.nextInt(pipes.size());
//		Map.Entry<EnumFacing, IPipeTile> pipe = pipes.get(choice);
//		if (pipe.getValue().injectItem(stackToMove, false, pipe.getKey().getOpposite(), null) > 0) {
//			if (removeOneItem(source, stackToMove) != null) {
//				pipe.getValue().injectItem(stackToMove, true, pipe.getKey().getOpposite(), null);
//				return true;
//			}
//		}
		return false;
	}

	/* REMOVAL */

	/**
	 * Removes a set of items from an inventory.
	 * Removes the exact items first if they exist, and then removes crafting equivalents.
	 * If the inventory doesn't have all the required items, returns false without removing anything.
	 * If stowContainer is true, items with containers will have their container stowed.
	 */
	public static boolean removeSets(IInventory inventory, int count, ItemStack[] set, @Nullable EntityPlayer player, boolean stowContainer, boolean oreDictionary, boolean craftingTools, boolean doRemove) {
		ItemStack[] stock = getStacks(inventory);

		if (doRemove) {
			ItemStack[] removed = removeSets(inventory, count, set, player, stowContainer, oreDictionary, craftingTools);
			return removed != null && removed.length >= count;
		} else {
			return ItemStackUtil.containsSets(set, stock, oreDictionary, craftingTools) >= count;
		}
	}

	public static ItemStack[] removeSets(IInventory inventory, int count, ItemStack[] set, @Nullable EntityPlayer player, boolean stowContainer, boolean oreDictionary, boolean craftingTools) {
		ItemStack[] removed = new ItemStack[set.length];
		ItemStack[] stock = getStacks(inventory);

		if (ItemStackUtil.containsSets(set, stock, oreDictionary, craftingTools) < count) {
			return null;
		}

		for (int i = 0; i < set.length; i++) {
			if (set[i] == null) {
				continue;
			}
			ItemStack stackToRemove = set[i].copy();
			stackToRemove.stackSize *= count;

			// try to remove the exact stack first
			ItemStack removedStack = removeStack(inventory, stackToRemove, player, stowContainer, false, false);
			if (removedStack == null) {
				// remove crafting equivalents next
				removedStack = removeStack(inventory, stackToRemove, player, stowContainer, oreDictionary, craftingTools);
			}

			removed[i] = removedStack;
		}
		return removed;
	}

	/**
	 * Private Helper for removeSetsFromInventory. Assumes removal is possible.
	 */
	private static ItemStack removeStack(IInventory inventory, ItemStack stackToRemove, @Nullable EntityPlayer player, boolean stowContainer, boolean oreDictionary, boolean craftingTools) {
		for (int j = 0; j < inventory.getSizeInventory(); j++) {
			ItemStack stackInSlot = inventory.getStackInSlot(j);
			if (stackInSlot == null) {
				continue;
			}

			if (!ItemStackUtil.isCraftingEquivalent(stackToRemove, stackInSlot, oreDictionary, craftingTools)) {
				continue;
			}

			ItemStack removed = inventory.decrStackSize(j, stackToRemove.stackSize);
			stackToRemove.stackSize -= removed.stackSize;

			if (stowContainer && stackToRemove.getItem().hasContainerItem(stackToRemove)) {
				stowContainerItem(removed, inventory, j, player);
			}

			if (stackToRemove.stackSize == 0) {
				return removed;
			}
		}
		return null;
	}

	/* CONTAINS */

	public static boolean contains(IInventory inventory, ItemStack[] query) {
		return contains(inventory, query, 0, inventory.getSizeInventory());
	}

	public static boolean contains(IInventory inventory, ItemStack[] query, int startSlot, int slots) {
		ItemStack[] stock = getStacks(inventory, startSlot, slots);
		return ItemStackUtil.containsSets(query, stock) > 0;
	}

	public static boolean containsPercent(IInventory inventory, float percent) {
		return containsPercent(inventory, percent, 0, inventory.getSizeInventory());
	}

	public static boolean containsPercent(IInventory inventory, float percent, int slot1, int length) {
		int amount = 0;
		int stackMax = 0;
		for (ItemStack itemStack : getStacks(inventory, slot1, length)) {
			if (itemStack == null) {
				stackMax += 64;
				continue;
			}

			amount += itemStack.stackSize;
			stackMax += itemStack.getMaxStackSize();
		}
		if (stackMax == 0) {
			return false;
		}
		return (float) amount / (float) stackMax >= percent;
	}

	public static boolean isEmpty(IInventory inventory, int slotStart, int slotCount) {
		for (int i = slotStart; i < slotStart + slotCount; i++) {
			if (inventory.getStackInSlot(i) != null) {
				return false;
			}
		}
		return true;
	}

	public static ItemStack[] getStacks(IInventory inventory) {
		ItemStack[] stacks = new ItemStack[inventory.getSizeInventory()];
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			stacks[i] = inventory.getStackInSlot(i);
		}
		return stacks;
	}

	public static ItemStack[] getStacks(IInventory inventory, int slot1, int length) {
		ItemStack[] result = new ItemStack[length];
		for (int i = slot1; i < slot1 + length; i++) {
			result[i - slot1] = inventory.getStackInSlot(i);
		}
		return result;
	}

	public static boolean tryAddStacksCopy(IInventory inventory, ItemStack[] stacks, int startSlot, int slots, boolean all) {

		for (ItemStack stack : stacks) {
			if (stack == null) {
				continue;
			}

			if (!tryAddStack(inventory, stack.copy(), startSlot, slots, all)) {
				return false;
			}
		}

		return true;
	}

	public static boolean tryAddStack(IInventory inventory, ItemStack stack, boolean all) {
		return tryAddStack(inventory, stack, 0, inventory.getSizeInventory(), all, true);
	}

	public static boolean tryAddStack(IInventory inventory, ItemStack stack, boolean all, boolean doAdd) {
		return tryAddStack(inventory, stack, 0, inventory.getSizeInventory(), all, doAdd);
	}

	/**
	 * Tries to add a stack to the specified slot range.
	 */
	public static boolean tryAddStack(IInventory inventory, ItemStack stack, int startSlot, int slots, boolean all) {
		return tryAddStack(inventory, stack, startSlot, slots, all, true);
	}

	public static boolean tryAddStack(IInventory inventory, ItemStack stack, int startSlot, int slots, boolean all, boolean doAdd) {
		int added = addStack(inventory, stack, startSlot, slots, false);
		boolean success = all ? added == stack.stackSize : added > 0;

		if (success && doAdd) {
			addStack(inventory, stack, startSlot, slots, true);
		}
		
		return success;
	}

	public static int addStack(IInventory inventory, ItemStack stack, boolean doAdd) {
		return addStack(inventory, stack, 0, inventory.getSizeInventory(), doAdd);
	}

	public static int addStack(IInventory inventory, ItemStack stack, int startSlot, int slots, boolean doAdd) {

		int added = 0;
		// Add to existing stacks first
		for (int i = startSlot; i < startSlot + slots; i++) {

			ItemStack inventoryStack = inventory.getStackInSlot(i);
			// Empty slot. Add
			if (inventoryStack == null || inventoryStack.getItem() == null) {
				continue;
			}

			// Already occupied by different item, skip this slot.
			if (!inventoryStack.isStackable()) {
				continue;
			}
			if (!inventoryStack.isItemEqual(stack)) {
				continue;
			}
			if (!ItemStack.areItemStackTagsEqual(inventoryStack, stack)) {
				continue;
			}

			int remain = stack.stackSize - added;
			int space = inventoryStack.getMaxStackSize() - inventoryStack.stackSize;
			// No space left, skip this slot.
			if (space <= 0) {
				continue;
			}
			// Enough space
			if (space >= remain) {
				if (doAdd) {
					inventoryStack.stackSize += remain;
				}
				return stack.stackSize;
			}

			// Not enough space
			if (doAdd) {
				inventoryStack.stackSize = inventoryStack.getMaxStackSize();
			}

			added += space;
		}

		if (added >= stack.stackSize) {
			return added;
		}

		for (int i = startSlot; i < startSlot + slots; i++) {
			if (inventory.getStackInSlot(i) != null) {
				continue;
			}

			if (doAdd) {
				inventory.setInventorySlotContents(i, stack.copy());
				inventory.getStackInSlot(i).stackSize = stack.stackSize - added;
			}
			return stack.stackSize;

		}

		return added;
	}

	public static boolean stowInInventory(ItemStack itemstack, IInventory inventory, boolean doAdd) {
		return stowInInventory(itemstack, inventory, doAdd, 0, inventory.getSizeInventory());
	}

	public static boolean stowInInventory(ItemStack itemstack, IInventory inventory, boolean doAdd, int slot1, int count) {

		boolean added = false;

		for (int i = slot1; i < slot1 + count; i++) {
			ItemStack inventoryStack = inventory.getStackInSlot(i);

			// Grab those free slots
			if (inventoryStack == null) {
				if (doAdd) {
					inventory.setInventorySlotContents(i, itemstack.copy());
					itemstack.stackSize = 0;
				}
				return true;
			}

			// Already full
			if (inventoryStack.stackSize >= inventoryStack.getMaxStackSize()) {
				continue;
			}

			// Not same type
			if (!inventoryStack.isItemEqual(itemstack)) {
				continue;
			}
			if (!ItemStack.areItemStackTagsEqual(inventoryStack, itemstack)) {
				continue;
			}

			int space = inventoryStack.getMaxStackSize() - inventoryStack.stackSize;

			// Enough space to add all
			if (space > itemstack.stackSize) {
				if (doAdd) {
					inventoryStack.stackSize += itemstack.stackSize;
					itemstack.stackSize = 0;
				}
				return true;
				// Only part can be added
			} else {
				if (doAdd) {
					inventoryStack.stackSize = inventoryStack.getMaxStackSize();
					itemstack.stackSize -= space;
				}
				added = true;
			}

		}

		return added;
	}

	public static void stowContainerItem(ItemStack itemstack, IInventory stowing, int slotIndex, @Nullable EntityPlayer player) {
		if (!itemstack.getItem().hasContainerItem(itemstack)) {
			return;
		}

		ItemStack container = ForgeHooks.getContainerItem(itemstack);
		if (container != null) {
			if (!tryAddStack(stowing, container, slotIndex, 1, true)) {
				if (!tryAddStack(stowing, container, true) && player != null) {
					player.dropItem(container, true);
				}
			}
		}
	}

	public static void deepCopyInventoryContents(IInventory source, IInventory destination) {
		if (source == null || destination == null) {
			throw new IllegalArgumentException("Inventory can't be null. Source: " + source + ", Destination: " + destination);
		}
		if (source.getSizeInventory() != destination.getSizeInventory()) {
			throw new IllegalArgumentException("Inventory sizes do not match. Source: " + source + ", Destination: " + destination);
		}

		for (int i = 0; i < source.getSizeInventory(); i++) {
			ItemStack stack = source.getStackInSlot(i);
			if (stack != null) {
				stack = stack.copy();
			}
			destination.setInventorySlotContents(i, stack);
		}
	}

	public static void dropInventory(IInventory inventory, World world, double x, double y, double z) {
		if (inventory == null) {
			return;
		}

		// Release inventory
		for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
			ItemStack itemstack = inventory.getStackInSlot(slot);
			dropItemStackFromInventory(itemstack, world, x, y, z);
			inventory.setInventorySlotContents(slot, null);
		}
	}
	
	public static void dropInventory(IInventory inventory, World world, BlockPos pos) {
		dropInventory(inventory, world, pos.getX(), pos.getY(), pos.getZ());
	}

	public static void dropSockets(ISocketable socketable, World world, double x, double y, double z) {
		for (int slot = 0; slot < socketable.getSocketCount(); slot++) {
			ItemStack itemstack = socketable.getSocket(slot);
			dropItemStackFromInventory(itemstack, world, x, y, z);
			socketable.setSocket(slot, null);
		}
	}
	
	public static void dropSockets(ISocketable socketable, World world, BlockPos pos) {
		dropSockets(socketable, world, pos.getX(), pos.getY(), pos.getZ());
	}

	public static void dropItemStackFromInventory(ItemStack itemStack, World world, double x, double y, double z) {
		if (itemStack == null) {
			return;
		}

		float f = world.rand.nextFloat() * 0.8F + 0.1F;
		float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
		float f2 = world.rand.nextFloat() * 0.8F + 0.1F;

		while (itemStack.stackSize > 0) {
			int stackPartial = world.rand.nextInt(21) + 10;
			if (stackPartial > itemStack.stackSize) {
				stackPartial = itemStack.stackSize;
			}
			ItemStack drop = itemStack.splitStack(stackPartial);
			EntityItem entityitem = new EntityItem(world, x + f, y + f1, z + f2, drop);
			float accel = 0.05F;
			entityitem.motionX = (float) world.rand.nextGaussian() * accel;
			entityitem.motionY = (float) world.rand.nextGaussian() * accel + 0.2F;
			entityitem.motionZ = (float) world.rand.nextGaussian() * accel;
			world.spawnEntityInWorld(entityitem);
		}
	}

	/* NBT */
	public static void readFromNBT(IInventory inventory, NBTTagCompound nbttagcompound) {
		if (!nbttagcompound.hasKey(inventory.getName())) {
			return;
		}

		NBTTagList nbttaglist = nbttagcompound.getTagList(inventory.getName(), 10);

		for (int j = 0; j < nbttaglist.tagCount(); ++j) {
			NBTTagCompound nbttagcompound2 = nbttaglist.getCompoundTagAt(j);
			int index = nbttagcompound2.getByte("Slot");
			inventory.setInventorySlotContents(index, ItemStack.loadItemStackFromNBT(nbttagcompound2));
		}
	}
	
	public static void writeToNBT(IInventory inventory, NBTTagCompound nbttagcompound) {
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (inventory.getStackInSlot(i) != null) {
				NBTTagCompound nbttagcompound2 = new NBTTagCompound();
				nbttagcompound2.setByte("Slot", (byte) i);
				inventory.getStackInSlot(i).writeToNBT(nbttagcompound2);
				nbttaglist.appendTag(nbttagcompound2);
			}
		}
		nbttagcompound.setTag(inventory.getName(), nbttaglist);
	}
}
