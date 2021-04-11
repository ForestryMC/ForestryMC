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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Created by CovertJaguar on 3/6/2016 for Railcraft.
 */
public abstract class InvWrapperBase implements IInventory {

	private final IInventory inv;
	private boolean checkItems = true;

	public InvWrapperBase(IInventory inv) {
		this(inv, true);
	}

	public InvWrapperBase(IInventory inv, boolean checkItems) {
		this.inv = inv;
		this.checkItems = checkItems;
	}

	public IInventory getBaseInventory() {
		return inv;
	}

	@Override
	public int getContainerSize() {
		return inv.getContainerSize();
	}

	@Override
	public ItemStack getItem(int slot) {
		return inv.getItem(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		return inv.removeItem(slot, amount);
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return inv.removeItemNoUpdate(slot);
	}

	@Override
	public void setItem(int slot, ItemStack itemstack) {
		inv.setItem(slot, itemstack);
	}

	//TODO inventory name
	//	@Override
	//	public String getName() {
	//		return inv.getName();
	//	}
	//
	//	@Override
	//	public ITextComponent getDisplayName() {
	//		return inv.getDisplayName();
	//	}

	@Override
	public int getMaxStackSize() {
		return inv.getMaxStackSize();
	}

	@Override
	public void setChanged() {
		inv.setChanged();
	}

	@Override
	public boolean stillValid(PlayerEntity PlayerEntity) {
		return inv.stillValid(PlayerEntity);
	}

	@Override
	public void startOpen(PlayerEntity player) {
		inv.startOpen(player);
	}

	@Override
	public void stopOpen(PlayerEntity player) {
		inv.stopOpen(player);
	}

	//TODO inventory name
	//	@Override
	//	public boolean hasCustomName() {
	//		return inv.hasCustomName();
	//	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return !checkItems || inv.canPlaceItem(slot, stack);
	}

	//TODO inventory field
	//	@Override
	//	public int getField(int id) {
	//		return inv.getField(id);
	//	}
	//
	//	@Override
	//	public void setField(int id, int value) {
	//		inv.setField(id, value);
	//	}
	//
	//	@Override
	//	public int getFieldCount() {
	//		return inv.getFieldCount();
	//	}

	@Override
	public void clearContent() {
		inv.clearContent();
	}

	public boolean checkItems() {
		return checkItems;
	}
}

