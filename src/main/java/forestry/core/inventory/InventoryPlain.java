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
package forestry.core.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.core.utils.InventoryUtil;

public class InventoryPlain implements IInventory, INbtWritable, INbtReadable {

	private final NonNullList<ItemStack> contents;
	private final String name;
	private final int stackLimit;

	public InventoryPlain(int size, String name, int stackLimit) {
		this.contents = NonNullList.withSize(size, ItemStack.EMPTY);
		this.name = name;
		this.stackLimit = stackLimit;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : contents) {
			if (!stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public NonNullList<ItemStack> getContents() {
		return contents;
	}

	@Override
	public int getContainerSize() {
		return contents.size();
	}

	@Override
	public ItemStack getItem(int slotId) {
		return contents.get(slotId);
	}

	@Override
	public ItemStack removeItem(int slotId, int count) {
		ItemStack itemStack = contents.get(slotId);
		if (itemStack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		return itemStack.split(count);
	}

	@Override
	public void setItem(int slotId, ItemStack itemstack) {
		contents.set(slotId, itemstack);
	}

	@Override
	public int getMaxStackSize() {
		return stackLimit;
	}

	@Override
	public void setChanged() {
	}

	@Override
	public boolean stillValid(PlayerEntity PlayerEntity) {
		return false;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slotIndex) {
		return this.getItem(slotIndex);
	}

	@Override
	public boolean canPlaceItem(int i, ItemStack itemstack) {
		return true;
	}

	/* INBTagable */
	@Override
	public void read(CompoundNBT CompoundNBT) {
		InventoryUtil.readFromNBT(this, name, CompoundNBT);
	}

	@Override
	public CompoundNBT write(CompoundNBT CompoundNBT) {
		InventoryUtil.writeToNBT(this, name, CompoundNBT);
		return CompoundNBT;
	}

	/* Fields */
	@Override
	public void clearContent() {
		contents.clear();
	}
}
