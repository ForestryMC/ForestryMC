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
package forestry.core.inventory.wrappers;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Wrapper class used to specify part of an existing inventory to be treated as
 * a complete inventory. Used primarily to map a side of an ISidedInventory, but
 * it is also helpful for complex inventories such as the Tunnel Bore.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class InventoryMapper extends InvWrapperBase implements IInventory {

	private final IInventory inv;
	private final int start;
	private final int size;
	private int stackSizeLimit = -1;

	public InventoryMapper(IInventory inv) {
		this(inv, 0, inv.getContainerSize(), true);
	}

	public InventoryMapper(IInventory inv, boolean checkItems) {
		this(inv, 0, inv.getContainerSize(), checkItems);
	}

	/**
	 * Creates a new InventoryMapper
	 *
	 * @param inv   The backing inventory
	 * @param start The starting index
	 * @param size  The size of the new inventory, take care not to exceed the
	 *              end of the backing inventory
	 */
	public InventoryMapper(IInventory inv, int start, int size) {
		this(inv, start, size, true);
	}

	public InventoryMapper(IInventory inv, int start, int size, boolean checkItems) {
		super(inv, checkItems);
		this.inv = inv;
		this.start = start;
		this.size = size;
	}

	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	public int getContainerSize() {
		return size;
	}

	@Override
	public ItemStack getItem(int slot) {
		return inv.getItem(start + slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		return inv.removeItem(start + slot, amount);
	}

	@Override
	public void setItem(int slot, ItemStack itemstack) {
		inv.setItem(start + slot, itemstack);
	}

	@Override
	public int getMaxStackSize() {
		return stackSizeLimit > 0 ? stackSizeLimit : inv.getMaxStackSize();
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return !checkItems() || inv.canPlaceItem(start + slot, stack);
	}

}
