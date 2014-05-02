/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

import buildcraft.api.inventory.ISpecialInventory;
import buildcraft.api.transport.PipeManager;

import forestry.core.inventory.filters.ArrayStackFilter;
import forestry.core.inventory.filters.IStackFilter;
import forestry.core.inventory.filters.InvertedStackFilter;
import forestry.core.inventory.filters.StackFilter;
import forestry.core.inventory.manipulators.InventoryManipulator;
import forestry.core.inventory.wrappers.ChestWrapper;
import forestry.core.inventory.wrappers.IInvSlot;
import forestry.core.inventory.wrappers.InventoryIterator;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.inventory.wrappers.SidedInventoryMapper;
import forestry.core.inventory.wrappers.SpecialInventoryMapper;
import forestry.core.utils.PlainInventory;

public abstract class InvTools {

	/*private static final String TAG_ID = "id";
	private static final String TAG_SIZE = "stackSize";
	private static final String TAG_DAMAGE = "Damage";
	private static final String TAG_COUNT = "Count";*/
	private static final String TAG_SLOT = "Slot";

	public static int getXOnSide(int x, ForgeDirection side) {
		return x + side.offsetX;
	}

	public static int getYOnSide(int y, ForgeDirection side) {
		return y + side.offsetY;
	}

	public static int getZOnSide(int z, ForgeDirection side) {
		return z + side.offsetZ;
	}

	public static boolean blockExistsOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return world.blockExists(getXOnSide(x, side), getYOnSide(y, side), getZOnSide(z, side));
	}

	public static int getBlockMetadataOnSide(IBlockAccess world, int i, int j, int k, ForgeDirection side) {
		return world.getBlockMetadata(getXOnSide(i, side), getYOnSide(j, side), getZOnSide(k, side));
	}

	public static Block getBlockOnSide(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return world.getBlock(getXOnSide(x, side), getYOnSide(y, side), getZOnSide(z, side));
	}

	public static TileEntity getBlockTileEntityOnSide(World world, int x, int y, int z, ForgeDirection side) {
		int sx = getXOnSide(x, side);
		int sy = getYOnSide(y, side);
		int sz = getZOnSide(z, side);
		if (world.blockExists(sx, sy, sz))
			return world.getTileEntity(sx, sy, sz);
		return null;
	}

	public static TileEntity getBlockTileEntityOnSide(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		int sx = getXOnSide(x, side);
		int sy = getYOnSide(y, side);
		int sz = getZOnSide(z, side);
		return world.getTileEntity(sx, sy, sz);
	}

	public static List<IInventory> getAdjacentInventories(World world, int i, int j, int k) {
		return getAdjacentInventories(world, i, j, k, null);
	}

	public static List<IInventory> getAdjacentInventories(World world, int i, int j, int k, Class<? extends IInventory> type) {
		List<IInventory> list = new ArrayList<IInventory>(5);
		for (int side = 0; side < 6; side++) {
			IInventory inv = getInventoryFromSide(world, i, j, k, ForgeDirection.getOrientation(side), type, null);
			if (inv != null)
				list.add(inv);
		}
		return list;
	}

	public static Map<Integer, IInventory> getAdjacentInventoryMap(World world, int i, int j, int k) {
		return getAdjacentInventoryMap(world, i, j, k, null);
	}

	public static Map<Integer, IInventory> getAdjacentInventoryMap(World world, int i, int j, int k, Class<? extends IInventory> type) {
		Map<Integer, IInventory> map = new TreeMap<Integer, IInventory>();
		for (int side = 0; side < 6; side++) {
			IInventory inv = getInventoryFromSide(world, i, j, k, ForgeDirection.getOrientation(side), type, null);
			if (inv != null)
				map.put(side, inv);
		}
		return map;
	}

	public static IInventory getInventoryFromSide(World world, int x, int y, int z, ForgeDirection side, final Class<? extends IInventory> type, final Class<? extends IInventory> exclude) {
		return getInventoryFromSide(world, x, y, z, side, new ITileFilter() {
			@Override
			public boolean matches(TileEntity tile) {
				if (type != null && !type.isAssignableFrom(tile.getClass()))
					return false;
				if (exclude != null && exclude.isAssignableFrom(tile.getClass()))
					return false;
				return true;
			}
		});
	}

	public static IInventory getInventoryFromSide(World world, int x, int y, int z, ForgeDirection side, ITileFilter filter) {
		TileEntity tile = getBlockTileEntityOnSide(world, x, y, z, side);
		if (tile == null || !(tile instanceof IInventory) || !filter.matches(tile))
			return null;
		return getInventoryFromTile(tile, side.getOpposite());
	}

	public static IInventory getInventoryFromTile(TileEntity tile, ForgeDirection side) {
		if (tile == null || !(tile instanceof IInventory))
			return null;

		if (!PipeManager.canExtractItems(null, tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord))
			return null;

		if (tile instanceof TileEntityChest) {
			TileEntityChest chest = (TileEntityChest) tile;
			return new ChestWrapper(chest);
		}
		return getInventory((IInventory) tile, side);
	}

	public static IInventory getInventory(IInventory inv, ForgeDirection side) {
		if (inv == null)
			return null;

		if (inv instanceof ISpecialInventory)
			inv = new SpecialInventoryMapper((ISpecialInventory) inv, side);
		else if (inv instanceof ISidedInventory)
			inv = new SidedInventoryMapper((ISidedInventory) inv, side);
		return inv;
	}

	public static int[] buildSlotArray(int start, int size) {
		int[] slots = new int[size];
		for (int i = 0; i < size; i++) {
			slots[i] = start + i;
		}
		return slots;
	}

	/*public static void addItemToolTip(ItemStack stack, String tag, String msg) {
		NBTTagCompound nbt = getItemData(stack);
		NBTTagCompound display = nbt.getCompoundTag("display");
		nbt.setTag("display", display);
		NBTTagList lore = display.getTagList("Lore", 8); // 8 = String
		display.setTag("Lore", lore);
		//lore.appendTag(new NBTTagString(tag, msg));
		lore.appendTag(new NBTTagString(msg));
	}*/

	public static NBTTagCompound getItemData(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
			stack.setTagCompound(nbt);
		}
		return nbt;
	}

	public static ItemStack depleteItem(ItemStack stack) {
		if (stack.stackSize == 1)
			return stack.getItem().getContainerItem(stack);
		else {
			stack.splitStack(1);
			return stack;
		}
	}

	public static ItemStack damageItem(ItemStack stack, int damage) {
		if (!stack.isItemStackDamageable())
			return stack;
		int curDamage = stack.getItemDamage();
		curDamage += damage;
		stack.setItemDamage(curDamage);
		if (stack.getItemDamage() > stack.getMaxDamage()) {
			stack.stackSize--;
			stack.setItemDamage(0);
		}
		if (stack.stackSize <= 0)
			stack = null;
		return stack;
	}

	public static void dropItem(ItemStack stack, World world, double x, double y, double z) {
		if (stack == null || stack.stackSize < 1)
			return;
		EntityItem entityItem = new EntityItem(world, x, y + 1.5, z, stack);
		entityItem.delayBeforeCanPickup = 10;
		world.spawnEntityInWorld(entityItem);
	}

	public static boolean isInventoryEmpty(IInventory inv, ForgeDirection side) {
		return isInventoryEmpty(getInventory(inv, side));
	}

	public static boolean isInventoryEmpty(IInventory inv) {
		ItemStack stack = null;
		for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
			stack = slot.getStackInSlot();
			if (stack != null)
				break;
		}
		return stack == null;
	}

	public static boolean isInventoryFull(IInventory inv, ForgeDirection side) {
		return isInventoryFull(getInventory(inv, side));
	}

	public static boolean isInventoryFull(IInventory inv) {
		ItemStack stack = null;
		for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
			stack = slot.getStackInSlot();
			if (stack == null)
				break;
		}
		return stack != null;
	}

	/**
	 * Counts the number of items.
	 *
	 * @param inv
	 * @return
	 */
	public static int countItems(IInventory inv) {
		int count = 0;
		for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
			ItemStack stack = slot.getStackInSlot();
			if (stack != null)
				count += stack.stackSize;
		}
		return count;
	}

	public static int countItems(IInventory inv, IStackFilter filter) {
		int count = 0;
		for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
			ItemStack stack = slot.getStackInSlot();
			if (stack != null && filter.matches(stack))
				count += stack.stackSize;
		}
		return count;
	}

	public static boolean numItemsMoreThan(IInventory inv, int amount) {
		int count = 0;
		for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
			ItemStack stack = slot.getStackInSlot();
			if (stack != null)
				count += stack.stackSize;
			if (count >= amount)
				return true;
		}
		return false;
	}

	/**
	 * Counts the number of items that match the filter. Ignores
	 * ISpecialInventory and ISidedInventory, and bypasses InventoryMapper to
	 * count from the base IInventory.
	 *
	 * @param inv
	 * @param filters
	 * @return
	 */
	public static int countItems(IInventory inv, ItemStack... filters) {
		if (inv instanceof InventoryMapper)
			inv = ((InventoryMapper) inv).getBaseInventory();
		boolean hasFilter = false;
		for (ItemStack filter : filters) {
			if (filter != null) {
				hasFilter = true;
				break;
			}
		}

		if (!hasFilter)
			return countItems(inv);

		int count = 0;
		ItemStack stack = null;
		for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
			stack = inv.getStackInSlot(slot);
			if (stack != null)
				for (ItemStack filter : filters) {
					if (filter != null && isItemEqual(stack, filter)) {
						count += stack.stackSize;
						break;
					}
				}
		}
		return count;
	}

	public static int countItems(Collection<IInventory> inventories, ItemStack... filter) {
		int count = 0;
		for (IInventory inv : inventories) {
			count += InvTools.countItems(inv, filter);
		}
		return count;
	}

	/**
	 * Returns true if the inventory contains the specified item.
	 *
	 * @param inv The IIinventory to check
	 * @param item The ItemStack to look for
	 * @return true is exists
	 */
	public static boolean containsItem(IInventory inv, ItemStack item) {
		return countItems(inv, item) > 0;
	}

	/**
	 * Returns a map backed by an
	 * <code>ItemStackMap</code> that lists the total number of each type of
	 * item in the inventory.
	 *
	 * @param inv The <code>IInventory</code> to generate the manifest for
	 * @return A <code>Map</code> that lists how many of each item is in * * *
	 * the <code>IInventory</code>
	 * @see ItemStackMap
	 */
	public static Map<ItemStack, Integer> getManifest(IInventory inv) {
		Map<ItemStack, Integer> manifest = new ItemStackMap<Integer>();
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack slot = inv.getStackInSlot(i);
			if (slot != null) {
				Integer count = manifest.get(slot);
				if (count == null)
					count = 0;
				count += slot.stackSize;
				manifest.put(slot, count);
			}
		}
		return manifest;
	}

	/**
	 * Attempts to move a single item from one inventory to another.
	 *
	 * @param source
	 * @param dest
	 * @return null if nothing was moved, the stack moved otherwise
	 */
	public static ItemStack moveOneItem(IInventory source, IInventory dest) {
		return moveOneItem(source, dest, StackFilter.ALL);
	}

	/**
	 * Attempts to move a single item from one inventory to another.
	 *
	 * @param source
	 * @param dest
	 * @param filer an ItemStack[] to match against
	 * @return null if nothing was moved, the stack moved otherwise
	 */
	public static ItemStack moveOneItem(IInventory source, IInventory dest, ItemStack... filters) {
		return moveOneItem(source, dest, new ArrayStackFilter(filters));
	}

	/**
	 * Attempts to move a single item from one inventory to another.
	 *
	 * @param source
	 * @param dest
	 * @param filer an IItemType to match against
	 * @return null if nothing was moved, the stack moved otherwise
	 */
	public static ItemStack moveOneItem(IInventory source, IInventory dest, IStackFilter filter) {
		InventoryManipulator imSource = InventoryManipulator.get(source);
		return imSource.moveItem(dest, filter);
	}

	/**
	 * Attempts to move one item from a collection of inventories.
	 *
	 * @param sources
	 * @param dest
	 * @param filters
	 * @return
	 */
	public static ItemStack moveOneItem(Collection<IInventory> sources, IInventory dest, ItemStack... filters) {
		for (IInventory inv : sources) {
			ItemStack moved = InvTools.moveOneItem(inv, dest, filters);
			if (moved != null)
				return moved;
		}
		return null;
	}

	/**
	 * Attempts to move one item from a collection of inventories.
	 *
	 * @param sources
	 * @param dest
	 * @param filters
	 * @return
	 */
	public static ItemStack moveOneItem(Collection<IInventory> sources, IInventory dest, IStackFilter filter) {
		for (IInventory inv : sources) {
			ItemStack moved = InvTools.moveOneItem(inv, dest, filter);
			if (moved != null)
				return moved;
		}
		return null;
	}

	/**
	 * Attempts to move one item to a collection of inventories.
	 *
	 * @param sources
	 * @param dest
	 * @param filters
	 * @return
	 */
	public static ItemStack moveOneItem(IInventory source, Collection<IInventory> destinations, ItemStack... filters) {
		for (IInventory dest : destinations) {
			ItemStack moved = InvTools.moveOneItem(source, dest, filters);
			if (moved != null)
				return moved;
		}
		return null;
	}

	/**
	 * Attempts to move a single item from one inventory to another.
	 *
	 * Will not move any items in the filter.
	 *
	 * @param source
	 * @param dest
	 * @param filer an ItemStack[] to exclude
	 * @return null if nothing was moved, the stack moved otherwise
	 */
	public static ItemStack moveOneItemExcept(IInventory source, IInventory dest, ItemStack... filters) {
		return moveOneItem(source, dest, new InvertedStackFilter(new ArrayStackFilter(filters)));
	}

	/**
	 * Attempts to move one item from a collection of inventories.
	 *
	 * @param sources
	 * @param dest
	 * @param filters
	 * @return
	 */
	public static ItemStack moveOneItemExcept(Collection<IInventory> sources, IInventory dest, ItemStack... filters) {
		for (IInventory inv : sources) {
			ItemStack moved = InvTools.moveOneItemExcept(inv, dest, filters);
			if (moved != null)
				return moved;
		}
		return null;
	}

	/**
	 * Attempts to move one item to a collection of inventories.
	 *
	 * @param sources
	 * @param dest
	 * @param filters
	 * @return
	 */
	public static ItemStack moveOneItemExcept(IInventory source, Collection<IInventory> destinations, ItemStack... filters) {
		for (IInventory dest : destinations) {
			ItemStack moved = InvTools.moveOneItemExcept(source, dest, filters);
			if (moved != null)
				return moved;
		}
		return null;
	}

	public static boolean isWildcard(ItemStack stack) {
		return isWildcard(stack.getItemDamage());
	}

	public static boolean isWildcard(int damage) {
		return damage == -1 || damage == OreDictionary.WILDCARD_VALUE;
	}

	/**
	 * A more robust item comparison function.
	 *
	 * Compares stackSize as well.
	 *
	 * Two null stacks will return true, unlike the other functions.
	 *
	 * This function is primarily intended to be used to track changes to an
	 * ItemStack.
	 *
	 * @param a An ItemStack
	 * @param b An ItemStack
	 * @return True if equal
	 */
	public static boolean isItemEqualStrict(ItemStack a, ItemStack b) {
		if (a == null && b == null)
			return true;
		if (a == null || b == null)
			return false;
		if (a.getItem() != b.getItem())
			return false;
		if (a.stackSize != b.stackSize)
			return false;
		if (a.getItemDamage() != b.getItemDamage())
			return false;
		if (a.stackTagCompound != null && !a.stackTagCompound.equals(b.stackTagCompound))
			return false;
		return true;
	}

	/**
	 * A more robust item comparison function.
	 *
	 * Compares stackSize as well.
	 *
	 * Two null stacks will return true, unlike the other functions.
	 *
	 * This function is primarily intended to be used to track changes to an
	 * ItemStack.
	 *
	 * @param a An ItemStack
	 * @param b An ItemStack
	 * @return True if equal
	 */
	public static boolean isItemEqualSemiStrict(ItemStack a, ItemStack b) {
		if (a == null && b == null)
			return true;
		if (a == null || b == null)
			return false;
		if (a.getItem() != b.getItem())
			return false;
		if (a.getItemDamage() != b.getItemDamage())
			return false;
		if (a.stackTagCompound != null && !a.stackTagCompound.equals(b.stackTagCompound))
			return false;
		return true;
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

	/**
	 * A more robust item comparison function. Supports items with damage = -1
	 * matching any sub-type.
	 *
	 * @param a An ItemStack
	 * @param b An ItemStack
	 * @return True if equal
	 */
	public static boolean isItemEqualIgnoreNBT(ItemStack a, ItemStack b) {
		return isItemEqual(a, b, true, false);
	}

	public static boolean isItemEqual(final ItemStack a, final ItemStack b, final boolean matchDamage, final boolean matchNBT) {
		if (a == null || b == null)
			return false;
		if (a.getItem() != b.getItem())
			return false;
		if (matchNBT && !ItemStack.areItemStackTagsEqual(a, b))
			return false;
		if (matchDamage && a.getHasSubtypes()) {
			if (isWildcard(a) || isWildcard(b))
				return true;
			if (a.getItemDamage() != b.getItemDamage())
				return false;
		}
		return true;
	}

	/**
	 * Returns true if the item is equal to any one of several possible matches.
	 *
	 * @param stack
	 * @param matches
	 * @return
	 */
	public static boolean isItemEqual(ItemStack stack, ItemStack... matches) {
		for (ItemStack match : matches) {
			if (isItemEqual(stack, match))
				return true;
		}
		return false;
	}

	/**
	 * Returns true if the item is equal to any one of several possible matches.
	 *
	 * @param stack
	 * @param matches
	 * @return
	 */
	public static boolean isItemEqual(ItemStack stack, Collection<ItemStack> matches) {
		for (ItemStack match : matches) {
			if (isItemEqual(stack, match))
				return true;
		}
		return false;
	}

	/**
	 * Places an ItemStack in a destination IInventory. Will attempt to move as
	 * much of the stack as possible, returning any remainder.
	 *
	 * @param stack The ItemStack to put in the inventory.
	 * @param dest The destination IInventory.
	 * @return Null if itemStack was completely moved, a new itemStack with
	 * remaining stackSize if part or none of the stack was moved.
	 */
	public static ItemStack moveItemStack(ItemStack stack, IInventory dest) {
		InventoryManipulator im = InventoryManipulator.get(dest);
		return im.addStack(stack);
	}

	/**
	 * Checks if there is room for the ItemStack in the inventory.
	 *
	 * @param stack The ItemStack
	 * @param dest The IInventory
	 * @return true if room for stack
	 */
	public static boolean isRoomForStack(ItemStack stack, IInventory dest) {
		if (stack == null || dest == null)
			return false;
		InventoryManipulator im = InventoryManipulator.get(dest);
		return im.canAddStack(stack);
	}

	/**
	 * Removes a up to numItems worth of items from the inventory, not caring
	 * about what the items are.
	 *
	 * @param inv
	 * @param numItems
	 * @return
	 */
	public static ItemStack[] removeItems(IInventory inv, int numItems) {
		if (inv instanceof ISpecialInventory)
			return ((ISpecialInventory) inv).extractItem(true, ForgeDirection.UNKNOWN, numItems);
		PlainInventory output = new PlainInventory(27, "temp");
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			if (numItems <= 0)
				break;
			ItemStack slot = inv.getStackInSlot(i);
			if (slot == null)
				continue;
			ItemStack removed = inv.decrStackSize(i, numItems);
			numItems -= removed.stackSize;
			ItemStack remainder = moveItemStack(removed, output);
			if (remainder != null) {
				moveItemStack(remainder, inv);
				numItems += remainder.stackSize;
				break;
			}
		}

		List<ItemStack> list = new LinkedList<ItemStack>();
		for (ItemStack stack : output.getContents()) {
			if (stack != null)
				list.add(stack);
		}
		return list.toArray(new ItemStack[0]);
	}

	/**
	 * Removes and returns a single item from the inventory.
	 *
	 * @param inv The inventory
	 * @return An ItemStack
	 */
	public static ItemStack removeOneItem(IInventory inv) {
		return removeOneItem(inv, StackFilter.ALL);
	}

	/**
	 * Removes and returns a single item from the inventory that matches the
	 * filter.
	 *
	 * @param inv The inventory
	 * @param filter ItemStack to match against
	 * @return An ItemStack
	 */
	public static ItemStack removeOneItem(IInventory inv, ItemStack... filter) {
		return removeOneItem(inv, new ArrayStackFilter(filter));
	}

	/**
	 * Removes and returns a single item from the inventory that matches the
	 * filter.
	 *
	 * @param inv The inventory
	 * @param filter EnumItemType to match against
	 * @return An ItemStack
	 */
	public static ItemStack removeOneItem(IInventory inv, IStackFilter filter) {
		InventoryManipulator im = InventoryManipulator.get(inv);
		return im.removeItem(filter);
	}

	/**
	 * Removes and returns a single item from the inventory that matches the
	 * filter.
	 *
	 * @param inv The inventory
	 * @param filter EnumItemType to match against
	 * @return An ItemStack
	 */
	public static ItemStack removeOneItem(Collection<IInventory> invs, IStackFilter filter) {
		for (IInventory inv : invs) {
			ItemStack stack = removeOneItem(inv, filter);
			if (stack != null)
				return stack;
		}
		return null;
	}

	public static void writeInvToNBT(IInventory inv, String tag, NBTTagCompound data) {
		NBTTagList list = new NBTTagList();
		for (byte slot = 0; slot < inv.getSizeInventory(); slot++) {
			ItemStack stack = inv.getStackInSlot(slot);
			if (stack != null) {
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setByte(TAG_SLOT, slot);
				writeItemToNBT(stack, itemTag);
				list.appendTag(itemTag);
			}
		}
		data.setTag(tag, list);
	}

	public static void readInvFromNBT(IInventory inv, String tag, NBTTagCompound data) {
		NBTTagList list = data.getTagList(tag, 10);
		for (byte entry = 0; entry < list.tagCount(); entry++) {
			NBTTagCompound itemTag = list.getCompoundTagAt(entry);
			int slot = itemTag.getByte(TAG_SLOT);
			if (slot >= 0 && slot < inv.getSizeInventory()) {
				ItemStack stack = ItemStack.loadItemStackFromNBT(itemTag);
				inv.setInventorySlotContents(slot, stack);
			}
		}
	}

	public static void writeItemToNBT(ItemStack stack, NBTTagCompound data) {
		if (stack == null || stack.stackSize <= 0)
			return;
		if (stack.stackSize > 127)
			stack.stackSize = 127;
		stack.writeToNBT(data);
	}
}
