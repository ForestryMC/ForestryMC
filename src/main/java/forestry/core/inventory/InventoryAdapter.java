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
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import forestry.core.config.Constants;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.core.utils.InventoryUtil;

/**
 * With permission from Krapht.
 */
public class InventoryAdapter implements IInventoryAdapter, IStreamable {

	private final IInventory inventory;
	private boolean allowAutomation = true;
	@Nullable
	private int[] slotMap;

	//private boolean debug = false;

	public InventoryAdapter(int size, String name) {
		this(size, name, 64);
	}

	public InventoryAdapter(int size, String name, int stackLimit) {
		this(new InventoryPlain(size, name, stackLimit));
	}

	public InventoryAdapter(IInventory inventory) {
		this.inventory = inventory;
		configureSided();
	}

	public InventoryAdapter disableAutomation() {
		this.allowAutomation = false;
		return this;
	}

	//	public InventoryAdapter enableDebug() {
	//		this.debug = true;
	//		return this;
	//	}

	/**
	 * @return Copy of this inventory. Stacks are copies.
	 */
	public InventoryAdapter copy() {
		InventoryAdapter copy = new InventoryAdapter(inventory.getSizeInventory(), inventory.getName(), inventory.getInventoryStackLimit());

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (!inventory.getStackInSlot(i).isEmpty()) {
				copy.setInventorySlotContents(i, inventory.getStackInSlot(i).copy());
			}
		}

		return copy;
	}

	/* IINVENTORY */
	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slotId) {
		return inventory.getStackInSlot(slotId);
	}

	@Override
	public ItemStack decrStackSize(int slotId, int count) {
		return inventory.decrStackSize(slotId, count);
	}

	@Override
	public void setInventorySlotContents(int slotId, ItemStack itemstack) {
		inventory.setInventorySlotContents(slotId, itemstack);
	}

	@Override
	public String getName() {
		return inventory.getName();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public void markDirty() {
		inventory.markDirty();
	}

	@Override
	public ItemStack removeStackFromSlot(int slotIndex) {
		return inventory.removeStackFromSlot(slotIndex);
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString("");
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		return true;
	}

	@Override
	public boolean isLocked(int slotIndex) {
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	/* ISIDEDINVENTORY */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if (allowAutomation && slotMap != null) {
			return slotMap;
		}
		return Constants.SLOTS_NONE;
	}

	private void configureSided() {
		int count = getSizeInventory();
		slotMap = new int[count];
		for (int i = 0; i < count; i++) {
			slotMap[i] = i;
		}
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
		return isItemValidForSlot(slot, stack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
		return false;
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		InventoryUtil.readFromNBT(this, nbttagcompound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		InventoryUtil.writeToNBT(this, nbttagcompound);
		return nbttagcompound;
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		data.writeInventory(inventory);
	}

	@Override
	public void readData(PacketBufferForestry data) throws IOException {
		data.readInventory(inventory);
	}

	/* FIELDS */
	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public void clear() {
	}
}
