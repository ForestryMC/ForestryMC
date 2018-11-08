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

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import forestry.core.tiles.IFilterSlotDelegate;

public abstract class ItemInventory implements IInventory, IFilterSlotDelegate, ICapabilityProvider {
	private static final String KEY_SLOTS = "Slots";
	private static final String KEY_UID = "UID";
	private static final Random rand = new Random();

	private final IItemHandler itemHandler = new InvWrapper(this);

	protected final EntityPlayer player;
	private final ItemStack parent;
	private final NonNullList<ItemStack> inventoryStacks;

	public ItemInventory(EntityPlayer player, int size, ItemStack parent) {
		Preconditions.checkArgument(!parent.isEmpty(), "Parent cannot be empty.");

		this.player = player;
		this.parent = parent;
		this.inventoryStacks = NonNullList.withSize(size, ItemStack.EMPTY);

		NBTTagCompound nbt = parent.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
			parent.setTagCompound(nbt);
		}
		setUID(nbt); // Set a uid to identify the itemStack on SMP

		NBTTagCompound nbtSlots = nbt.getCompoundTag(KEY_SLOTS);
		for (int i = 0; i < inventoryStacks.size(); i++) {
			String slotKey = getSlotNBTKey(i);
			if (nbtSlots.hasKey(slotKey)) {
				NBTTagCompound itemNbt = nbtSlots.getCompoundTag(slotKey);
				ItemStack itemStack = new ItemStack(itemNbt);
				inventoryStacks.set(i, itemStack);
			} else {
				inventoryStacks.set(i, ItemStack.EMPTY);
			}
		}
	}

	public static int getOccupiedSlotCount(ItemStack itemStack) {
		NBTTagCompound nbt = itemStack.getTagCompound();
		if (nbt == null) {
			return 0;
		}

		NBTTagCompound slotNbt = nbt.getCompoundTag(KEY_SLOTS);
		return slotNbt.getKeySet().size();
	}

	private void setUID(NBTTagCompound nbt) {
		if (!nbt.hasKey(KEY_UID)) {
			nbt.setInteger(KEY_UID, rand.nextInt());
		}
	}

	public boolean isParentItemInventory(ItemStack itemStack) {
		ItemStack parent = getParent();
		return isSameItemInventory(parent, itemStack);
	}

	protected ItemStack getParent() {
		for (EnumHand hand : EnumHand.values()) {
			ItemStack held = player.getHeldItem(hand);
			if (isSameItemInventory(held, parent)) {
				return held;
			}
		}
		return parent;
	}

	@Nullable
	protected EnumHand getHand() {
		for (EnumHand hand : EnumHand.values()) {
			ItemStack held = player.getHeldItem(hand);
			if (isSameItemInventory(held, parent)) {
				return hand;
			}
		}
		return null;
	}

	private static boolean isSameItemInventory(ItemStack base, ItemStack comparison) {
		if (base.isEmpty() || comparison.isEmpty()) {
			return false;
		}

		if (base.getItem() != comparison.getItem()) {
			return false;
		}

		NBTTagCompound baseTagCompound = base.getTagCompound();
		NBTTagCompound comparisonTagCompound = comparison.getTagCompound();
		if (baseTagCompound == null || comparisonTagCompound == null) {
			return false;
		}

		if (!baseTagCompound.hasKey(KEY_UID) || !comparisonTagCompound.hasKey(KEY_UID)) {
			return false;
		}

		int baseUID = baseTagCompound.getInteger(KEY_UID);
		int comparisonUID = comparisonTagCompound.getInteger(KEY_UID);
		return baseUID == comparisonUID;
	}

	private void writeToParentNBT() {
		ItemStack parent = getParent();

		NBTTagCompound nbt = parent.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
			parent.setTagCompound(nbt);
		}

		NBTTagCompound slotsNbt = new NBTTagCompound();
		for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack itemStack = getStackInSlot(i);
			if (!itemStack.isEmpty()) {
				String slotKey = getSlotNBTKey(i);
				NBTTagCompound itemNbt = new NBTTagCompound();
				itemStack.writeToNBT(itemNbt);
				slotsNbt.setTag(slotKey, itemNbt);
			}
		}

		nbt.setTag(KEY_SLOTS, slotsNbt);
		onWriteNBT(nbt);
	}

	private static String getSlotNBTKey(int i) {
		return Integer.toString(i, Character.MAX_RADIX);
	}

	protected void onWriteNBT(NBTTagCompound nbt) {
	}

	public void onSlotClick(int slotIndex, EntityPlayer player) {
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.inventoryStacks) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}


	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack itemstack = ItemStackHelper.getAndSplit(this.inventoryStacks, index, count);

		if (!itemstack.isEmpty()) {
			this.markDirty();
		}

		return itemstack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack itemstack) {
		inventoryStacks.set(index, itemstack);

		ItemStack parent = getParent();

		NBTTagCompound nbt = parent.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
			parent.setTagCompound(nbt);
		}

		NBTTagCompound slotNbt;
		if (!nbt.hasKey(KEY_SLOTS)) {
			slotNbt = new NBTTagCompound();
			nbt.setTag(KEY_SLOTS, slotNbt);
		} else {
			slotNbt = nbt.getCompoundTag(KEY_SLOTS);
		}

		String slotKey = getSlotNBTKey(index);

		if (itemstack.isEmpty()) {
			slotNbt.removeTag(slotKey);
		} else {
			NBTTagCompound itemNbt = new NBTTagCompound();
			itemstack.writeToNBT(itemNbt);

			slotNbt.setTag(slotKey, itemNbt);
		}
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventoryStacks.get(i);
	}

	@Override
	public int getSizeInventory() {
		return inventoryStacks.size();
	}

	@Override
	public String getName() {
		return "BeeBag";
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(getName());
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public final void markDirty() {
		writeToParentNBT();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public boolean hasCustomName() {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		return canSlotAccept(slotIndex, itemStack);
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {
		ItemStack toReturn = getStackInSlot(slot);

		if (!toReturn.isEmpty()) {
			setInventorySlotContents(slot, ItemStack.EMPTY);
		}

		return toReturn;
	}

	/* IFilterSlotDelegate */
	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		return true;
	}

	@Override
	public boolean isLocked(int slotIndex) {
		return false;
	}

	/* Fields */
	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemHandler);
		}
		return null;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}

	public IItemHandler getItemHandler() {
		return itemHandler;
	}
}
