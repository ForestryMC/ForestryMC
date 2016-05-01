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

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;

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

	private final IItemHandler capabilityHandler = new InvWrapper(this);
	private final EntityPlayer player;
	private final ItemStack parent;
	private final ItemStack[] inventoryStacks;

	public ItemInventory(EntityPlayer player, int size, ItemStack parent) {
		this.player = player;
		this.parent = parent;
		this.inventoryStacks = new ItemStack[size];

		setUID(); // Set a uid to identify the itemStack on SMP

		readFromNBT(parent.getTagCompound());
	}

	public static int getOccupiedSlotCount(ItemStack itemStack) {
		NBTTagCompound nbt = itemStack.getTagCompound();
		if (nbt == null) {
			return 0;
		}

		NBTTagCompound slotNbt = nbt.getCompoundTag(KEY_SLOTS);
		return slotNbt.getKeySet().size();
	}

	private void setUID() {
		ItemStack parent = getParent();

		if (parent.getTagCompound() == null) {
			parent.setTagCompound(new NBTTagCompound());
		}

		NBTTagCompound nbt = parent.getTagCompound();
		if (!nbt.hasKey(KEY_UID)) {
			nbt.setInteger(KEY_UID, rand.nextInt());
		}
	}

	public boolean isParentItemInventory(ItemStack itemStack) {
		ItemStack parent = getParent();
		return isSameItemInventory(parent, itemStack);
	}

	protected ItemStack getParent() {
		ItemStack equipped = player.getCurrentEquippedItem();
		if (isSameItemInventory(equipped, parent)) {
			return equipped;
		}
		return parent;
	}

	private static boolean isSameItemInventory(ItemStack base, ItemStack comparison) {
		if (base == null || comparison == null) {
			return false;
		}

		if (base.getItem() != comparison.getItem()) {
			return false;
		}

		if (!base.hasTagCompound() || !comparison.hasTagCompound()) {
			return false;
		}

		String baseUID = base.getTagCompound().getString(KEY_UID);
		String comparisonUID = comparison.getTagCompound().getString(KEY_UID);
		return baseUID != null && comparisonUID != null && baseUID.equals(comparisonUID);
	}

	public void readFromNBT(NBTTagCompound nbt) {

		if (nbt == null) {
			return;
		}

		NBTTagCompound nbtSlots = nbt.getCompoundTag(KEY_SLOTS);
		for (int i = 0; i < inventoryStacks.length; i++) {
			String slotKey = getSlotNBTKey(i);
			if (nbtSlots.hasKey(slotKey)) {
				NBTTagCompound itemNbt = nbtSlots.getCompoundTag(slotKey);
				ItemStack itemStack = ItemStack.loadItemStackFromNBT(itemNbt);
				inventoryStacks[i] = itemStack;
			} else {
				inventoryStacks[i] = null;
			}
		}
	}

	private void writeToParentNBT() {
		ItemStack parent = getParent();
		if (parent == null) {
			return;
		}

		NBTTagCompound nbt = parent.getTagCompound();
		NBTTagCompound slotsNbt = new NBTTagCompound();
		for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack itemStack = getStackInSlot(i);
			if (itemStack != null) {
				String slotKey = getSlotNBTKey(i);
				NBTTagCompound itemNbt = new NBTTagCompound();
				itemStack.writeToNBT(itemNbt);
				slotsNbt.setTag(slotKey, itemNbt);
			}
		}

		nbt.setTag(KEY_SLOTS, slotsNbt);
	}

	private static String getSlotNBTKey(int i) {
		return Integer.toString(i, Character.MAX_RADIX);
	}

	public void onSlotClick(EntityPlayer player) {
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		ItemStack stack = getStackInSlot(i);
		if (stack == null) {
			return null;
		}

		if (stack.stackSize <= j) {
			setInventorySlotContents(i, null);
			return stack;
		} else {
			ItemStack product = stack.splitStack(j);
			setInventorySlotContents(i, stack);
			return product;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if (itemstack != null && itemstack.stackSize == 0) {
			itemstack = null;
		}

		inventoryStacks[i] = itemstack;

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

		String slotKey = getSlotNBTKey(i);

		if (itemstack == null) {
			slotNbt.removeTag(slotKey);
		} else {
			NBTTagCompound itemNbt = new NBTTagCompound();
			itemstack.writeToNBT(itemNbt);

			slotNbt.setTag(slotKey, itemNbt);
		}
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventoryStacks[i];
	}

	@Override
	public int getSizeInventory() {
		return inventoryStacks.length;
	}
	
	@Override
	public String getName() {
		return "BeeBag";
	}
	
	@Override
	public IChatComponent getDisplayName() {
		return new ChatComponentText(getName());
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
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
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

		if (toReturn != null) {
			setInventorySlotContents(slot, null);
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
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			//noinspection unchecked
			return (T) capabilityHandler;
		}
		return null;
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}
}
