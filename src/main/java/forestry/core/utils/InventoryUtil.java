/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.core.utils;

import forestry.core.circuits.ISocketable;
import forestry.core.inventory.ItemHandlerInventoryManipulator;
import forestry.core.inventory.StandardStackFilters;
import forestry.core.tiles.AdjacentTileCache;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

//import net.minecraftforge.fml.common.Optional;
//import forestry.plugins.ForestryCompatPlugins;

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

    /**
     * Attempts to move a single item from the source inventory into a adjacent Buildcraft pipe.
     * If the attempt fails, the source Inventory will not be modified.
     *
     * @param source    The source inventory
     * @param tileCache The tile cache of the source block.
     * @return true if an item was inserted, otherwise false.
     */
    public static boolean moveOneItemToPipe(IItemHandler source, AdjacentTileCache tileCache) {
        return moveOneItemToPipe(source, tileCache, Direction.values());
    }

    public static boolean moveOneItemToPipe(IItemHandler source, AdjacentTileCache tileCache, Direction[] directions) {
        if (false) {//ModuleHelper.isModuleEnabled(ForestryCompatPlugins.ID, ForestryModuleUids.BUILDCRAFT_TRANSPORT)) {
            return internal_moveOneItemToPipe(source, tileCache, directions);
        }

        return false;
    }

    //TODO Buildcraft for 1.14+
    //	@Optional.Method(modid = "buildcraftapi_transport")
    private static boolean internal_moveOneItemToPipe(
            IItemHandler source,
            AdjacentTileCache tileCache,
            Direction[] directions
    ) {
        //		IInventory invClone = new InventoryCopy(source);
        //		ItemStack stackToMove = removeOneItem(invClone);
        //		if (stackToMove == null) {
        //			return false;
        //		}
        //		if (stackToMove.stackSize <= 0) {
        //			return false;
        //		}
        //
        //		List<Map.Entry<Direction, IPipeTile>> pipes = new ArrayList<>();
        //		boolean foundPipe = false;
        //		for (Direction side : directions) {
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
        //		Map.Entry<Direction, IPipeTile> pipe = pipes.get(choice);
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
    public static boolean removeSets(
            IInventory inventory,
            int count,
            NonNullList<ItemStack> set,
            @Nullable PlayerEntity player,
            boolean stowContainer,
            boolean craftingTools,
            boolean doRemove
    ) {
        NonNullList<ItemStack> stock = getStacks(inventory);

        if (doRemove) {
            NonNullList<ItemStack> removed = removeSets(
                    inventory,
                    count,
                    set,
                    player,
                    stowContainer,
                    craftingTools
            );
            return removed != null && removed.size() >= count;
        } else {
            return ItemStackUtil.containsSets(set, stock, craftingTools) >= count;
        }
    }

    public static boolean deleteExactSet(IInventory inventory, NonNullList<ItemStack> required) {
        NonNullList<ItemStack> offered = getStacks(inventory);
        NonNullList<ItemStack> condensedRequired = ItemStackUtil.condenseStacks(required);
        NonNullList<ItemStack> condensedOffered = ItemStackUtil.condenseStacks(offered);

        for (ItemStack req : condensedRequired) {
            if (!containsExactStack(req, condensedOffered)) {
                return false;
            }
        }

        for (ItemStack itemStack : condensedRequired) {
            deleteExactStack(inventory, itemStack);
        }
        return true;
    }

    private static boolean containsExactStack(ItemStack req, NonNullList<ItemStack> condensedOffered) {
        for (ItemStack offer : condensedOffered) {
            if (offer.getCount() >= req.getCount() && ItemStackUtil.areItemStacksEqualIgnoreCount(req, offer)) {
                return true;
            }
        }
        return false;
    }

    private static void deleteExactStack(IInventory inventory, ItemStack itemStack) {
        int count = itemStack.getCount();
        for (int j = 0; j < inventory.getSizeInventory(); j++) {
            ItemStack stackInSlot = inventory.getStackInSlot(j);
            if (!stackInSlot.isEmpty()) {
                if (ItemStackUtil.areItemStacksEqualIgnoreCount(itemStack, stackInSlot)) {
                    ItemStack removed = inventory.decrStackSize(j, count);
                    count -= removed.getCount();
                    if (count == 0) {
                        return;
                    }
                }
            }
        }
    }

    @Nullable
    public static NonNullList<ItemStack> removeSets(
            IInventory inventory,
            int count,
            NonNullList<ItemStack> set,
            @Nullable PlayerEntity player,
            boolean stowContainer,
            boolean craftingTools
    ) {
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
                    removedStack = removeStack(
                            inventory,
                            stackToRemove,
                            player,
                            stowContainer,
                            craftingTools
                    );
                }

                removed.set(i, removedStack);
            }
        }
        return removed;
    }

    private static ItemStack removeStack(
            IInventory inventory,
            ItemStack stackToRemove,
            @Nullable PlayerEntity player,
            boolean stowContainer,
            boolean craftingTools
    ) {
        for (int j = 0; j < inventory.getSizeInventory(); j++) {
            ItemStack stackInSlot = inventory.getStackInSlot(j);
            if (!stackInSlot.isEmpty()) {
                if (ItemStackUtil.isCraftingEquivalent(stackToRemove, stackInSlot, craftingTools)) {
                    ItemStack removed = inventory.decrStackSize(j, stackToRemove.getCount());
                    stackToRemove.shrink(removed.getCount());

                    if (stowContainer && stackToRemove.getItem().hasContainerItem(stackToRemove)) {
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

    public static boolean contains(IInventory inventory, NonNullList<ItemStack> query) {
        return contains(inventory, query, 0, inventory.getSizeInventory());
    }

    public static boolean contains(IInventory inventory, NonNullList<ItemStack> query, int startSlot, int slots) {
        NonNullList<ItemStack> stock = getStacks(inventory, startSlot, slots);
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

            amount += itemStack.getCount();
            stackMax += itemStack.getMaxStackSize();
        }
        if (stackMax == 0) {
            return false;
        }
        return (float) amount / (float) stackMax >= percent;
    }

    public static boolean isEmpty(IInventory inventory, int slotStart, int slotCount) {
        for (int i = slotStart; i < slotStart + slotCount; i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static NonNullList<ItemStack> getStacks(IInventory inventory) {
        NonNullList<ItemStack> stacks = NonNullList.withSize(inventory.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            stacks.set(i, inventory.getStackInSlot(i));
        }
        return stacks;
    }

    public static NonNullList<ItemStack> getStacks(IInventory inventory, int slot1, int length) {
        NonNullList<ItemStack> result = NonNullList.withSize(length, ItemStack.EMPTY);
        for (int i = slot1; i < slot1 + length; i++) {
            result.set(i - slot1, inventory.getStackInSlot(i));
        }
        return result;
    }

    public static NonNullList<String> getOreDictAsList(String[][] oreDicts) {
        NonNullList<String> result = NonNullList.withSize(9, "");
        if (oreDicts == null || oreDicts.length == 0) {
            return result;
        }
        for (int i = 0; i < oreDicts.length; i++) {
            for (int d = 0; d < oreDicts[i].length; d++) {
                result.set(i * 3 + d, oreDicts[d][i]);
            }
        }
        return result;
    }

    public static boolean tryAddStacksCopy(
            IInventory inventory,
            NonNullList<ItemStack> stacks,
            int startSlot,
            int slots,
            boolean all
    ) {

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

    public static boolean tryAddStack(
            IInventory inventory,
            ItemStack stack,
            int startSlot,
            int slots,
            boolean all,
            boolean doAdd
    ) {
        int added = addStack(inventory, stack, startSlot, slots, false);
        boolean success = all ? added == stack.getCount() : added > 0;

        if (success && doAdd) {
            addStack(inventory, stack, startSlot, slots, true);
        }

        return success;
    }

    public static int addStack(IInventory inventory, ItemStack stack, boolean doAdd) {
        return addStack(inventory, stack, 0, inventory.getSizeInventory(), doAdd);
    }

    public static int addStack(IInventory inventory, ItemStack stack, int startSlot, int slots, boolean doAdd) {
        if (stack.isEmpty()) {
            return 0;
        }

        int added = 0;
        // Add to existing stacks first
        for (int i = startSlot; i < startSlot + slots; i++) {

            ItemStack inventoryStack = inventory.getStackInSlot(i);
            // Empty slot. Add
            if (inventoryStack.isEmpty()) {
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
            if (inventory.getStackInSlot(i).isEmpty()) {
                if (doAdd) {
                    inventory.setInventorySlotContents(i, stack.copy());
                    inventory.getStackInSlot(i).setCount(stack.getCount() - added);
                }
                return stack.getCount();
            }
        }

        return added;
    }

    public static boolean stowInInventory(ItemStack itemstack, IInventory inventory, boolean doAdd) {
        return stowInInventory(itemstack, inventory, doAdd, 0, inventory.getSizeInventory());
    }

    public static boolean stowInInventory(
            ItemStack itemstack,
            IInventory inventory,
            boolean doAdd,
            int slot1,
            int count
    ) {

        boolean added = false;

        for (int i = slot1; i < slot1 + count; i++) {
            ItemStack inventoryStack = inventory.getStackInSlot(i);

            // Grab those free slots
            if (inventoryStack.isEmpty()) {
                if (doAdd) {
                    inventory.setInventorySlotContents(i, itemstack.copy());
                    itemstack.setCount(0);
                }
                return true;
            }

            // Already full
            if (inventoryStack.getCount() >= inventoryStack.getMaxStackSize()) {
                continue;
            }

            // Not same type
            if (!inventoryStack.isItemEqual(itemstack)) {
                continue;
            }
            if (!ItemStack.areItemStackTagsEqual(inventoryStack, itemstack)) {
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

    public static void stowContainerItem(
            ItemStack itemstack,
            IInventory stowing,
            int slotIndex,
            @Nullable PlayerEntity player
    ) {
        if (!itemstack.getItem().hasContainerItem(itemstack)) {
            return;
        }

        ItemStack container = ForgeHooks.getContainerItem(itemstack);
        if (!container.isEmpty()) {
            if (!tryAddStack(stowing, container, slotIndex, 1, true)) {
                if (!tryAddStack(stowing, container, true) && player != null) {
                    player.dropItem(container, true);
                }
            }
        }
    }

    public static void deepCopyInventoryContents(IInventory source, IInventory destination) {
        if (source.getSizeInventory() != destination.getSizeInventory()) {
            throw new IllegalArgumentException(
                    "Inventory sizes do not match. Source: " + source + ", Destination: " + destination);
        }

        for (int i = 0; i < source.getSizeInventory(); i++) {
            ItemStack stack = source.getStackInSlot(i);
            if (!stack.isEmpty()) {
                stack = stack.copy();
            }
            destination.setInventorySlotContents(i, stack);
        }
    }

    public static void dropInventory(IInventory inventory, World world, double x, double y, double z) {
        // Release inventory
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack itemstack = inventory.getStackInSlot(slot);
            dropItemStackFromInventory(itemstack, world, x, y, z);
            inventory.setInventorySlotContents(slot, ItemStack.EMPTY);
        }
    }

    public static void dropInventory(IInventory inventory, World world, BlockPos pos) {
        dropInventory(inventory, world, pos.getX(), pos.getY(), pos.getZ());
    }

    public static void dropSockets(ISocketable socketable, World world, double x, double y, double z) {
        for (int slot = 0; slot < socketable.getSocketCount(); slot++) {
            ItemStack itemstack = socketable.getSocket(slot);
            dropItemStackFromInventory(itemstack, world, x, y, z);
            socketable.setSocket(slot, ItemStack.EMPTY);
        }
    }

    public static void dropSockets(ISocketable socketable, World world, BlockPos pos) {
        dropSockets(socketable, world, pos.getX(), pos.getY(), pos.getZ());
    }

    public static void dropItemStackFromInventory(ItemStack itemStack, World world, double x, double y, double z) {
        if (itemStack.isEmpty()) {
            return;
        }

        float f = world.rand.nextFloat() * 0.8F + 0.1F;
        float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
        float f2 = world.rand.nextFloat() * 0.8F + 0.1F;

        while (!itemStack.isEmpty()) {
            int stackPartial = world.rand.nextInt(21) + 10;
            if (stackPartial > itemStack.getCount()) {
                stackPartial = itemStack.getCount();
            }
            ItemStack drop = itemStack.split(stackPartial);
            ItemEntity entityitem = new ItemEntity(world, x + f, y + f1, z + f2, drop);
            double accel = 0.05D;
            //TODO - hopefully correct I think
            entityitem.setVelocity(
                    world.rand.nextGaussian() * accel,
                    world.rand.nextGaussian() * accel + 0.2F,
                    world.rand.nextGaussian() * accel
            );
            world.addEntity(entityitem);
        }
    }

    /* NBT */

    /**
     * The database has an inventory large enough that int must be used here instead of byte
     */
    public static void readFromNBT(IInventory inventory, String name, CompoundNBT compoundNBT) {
        if (!compoundNBT.contains(name)) {
            return;
        }

        ListNBT nbttaglist = compoundNBT.getList(name, 10);

        for (int j = 0; j < nbttaglist.size(); ++j) {
            CompoundNBT compoundNBT2 = nbttaglist.getCompound(j);
            int index = compoundNBT2.getInt("Slot");
            inventory.setInventorySlotContents(index, ItemStack.read(compoundNBT2));
        }
    }

    public static void writeToNBT(IInventory inventory, String name, CompoundNBT compoundNBT) {
        ListNBT nbttaglist = new ListNBT();
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) {
                CompoundNBT compoundNBT2 = new CompoundNBT();
                compoundNBT2.putInt("Slot", i);
                inventory.getStackInSlot(i).write(compoundNBT2);
                nbttaglist.add(compoundNBT2);
            }
        }
        compoundNBT.put(name, nbttaglist);
    }
}
