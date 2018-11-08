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

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import forestry.core.config.Constants;

public class FakeInventoryAdapter implements IInventoryAdapter {
	@Nullable
	private static FakeInventoryAdapter instance;

	public static FakeInventoryAdapter instance() {
		if (instance == null) {
			instance = new FakeInventoryAdapter();
		}
		return instance;
	}

	private FakeInventoryAdapter() {

	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		return false;
	}

	@Override
	public boolean isLocked(int slotIndex) {
		return false;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return Constants.SLOTS_NONE;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return false;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int p_70301_1_) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {

	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(getName());
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 0;
	}

	@Override
	public void markDirty() {

	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		return nbttagcompound;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
	}

}
