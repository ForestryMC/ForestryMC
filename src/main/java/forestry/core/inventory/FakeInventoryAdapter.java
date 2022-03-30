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

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;

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
	public int[] getSlotsForFace(Direction side) {
		return Constants.SLOTS_NONE;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return false;
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
		return false;
	}

	@Override
	public int getContainerSize() {
		return 0;
	}

	@Override
	public ItemStack getItem(int p_70301_1_) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setItem(int p_70299_1_, ItemStack p_70299_2_) {

	}

	//TODO inventory name
	//	@Override
	//	public ITextComponent getDisplayName() {
	//		return new StringTextComponent(getName());
	//	}
	//
	//	@Override
	//	public String getName() {
	//		return "";
	//	}
	//
	//	@Override
	//	public boolean hasCustomName() {
	//		return false;
	//	}

	@Override
	public int getMaxStackSize() {
		return 0;
	}

	@Override
	public void setChanged() {

	}

	@Override
	public boolean stillValid(Player player) {
		return false;
	}

	@Override
	public void startOpen(Player player) {

	}

	@Override
	public void stopOpen(Player player) {

	}

	@Override
	public boolean canPlaceItem(int p_94041_1_, ItemStack p_94041_2_) {
		return false;
	}

	@Override
	public void read(CompoundTag CompoundNBT) {

	}

	@Override
	public CompoundTag write(CompoundTag CompoundNBT) {
		return CompoundNBT;
	}

	//TODO inventory fields
	//	@Override
	//	public int getField(int id) {
	//		return 0;
	//	}
	//
	//	@Override
	//	public void setField(int id, int value) {
	//	}
	//
	//	@Override
	//	public int getFieldCount() {
	//		return 0;
	//	}

	@Override
	public void clearContent() {
	}

}
