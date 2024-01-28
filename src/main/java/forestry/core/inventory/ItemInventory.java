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

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import forestry.core.tiles.IFilterSlotDelegate;

public abstract class ItemInventory implements Container, IFilterSlotDelegate, ICapabilityProvider {
	private static final String KEY_SLOTS = "Slots";
	private static final String KEY_UID = "UID";
	private static final Random rand = new Random();

	private final IItemHandler itemHandler = new InvWrapper(this);

	protected final Player player;
	private ItemStack parent;    //TODO not final any more. Is this a problem
	private final NonNullList<ItemStack> inventoryStacks;

	public ItemInventory(Player player, int size, ItemStack parent) {
		Preconditions.checkArgument(!parent.isEmpty(), "Parent cannot be empty.");

		this.player = player;
		this.parent = parent;
		this.inventoryStacks = NonNullList.withSize(size, ItemStack.EMPTY);

		CompoundTag nbt = parent.getTag();
		if (nbt == null) {
			nbt = new CompoundTag();
			parent.setTag(nbt);
		}
		setUID(nbt); // Set a uid to identify the itemStack on SMP

		CompoundTag nbtSlots = nbt.getCompound(KEY_SLOTS);
		for (int i = 0; i < inventoryStacks.size(); i++) {
			String slotKey = getSlotNBTKey(i);
			if (nbtSlots.contains(slotKey)) {
				CompoundTag itemNbt = nbtSlots.getCompound(slotKey);
				ItemStack itemStack = ItemStack.of(itemNbt);
				inventoryStacks.set(i, itemStack);
			} else {
				inventoryStacks.set(i, ItemStack.EMPTY);
			}
		}
	}

	public static int getOccupiedSlotCount(ItemStack itemStack) {
		CompoundTag nbt = itemStack.getTag();
		if (nbt == null) {
			return 0;
		}

		CompoundTag slotNbt = nbt.getCompound(KEY_SLOTS);
		return slotNbt.size();
	}

	private void setUID(CompoundTag nbt) {
		if (!nbt.contains(KEY_UID)) {
			nbt.putInt(KEY_UID, rand.nextInt());
		}
	}

	public boolean isParentItemInventory(ItemStack itemStack) {
		ItemStack parent = getParent();
		return isSameItemInventory(parent, itemStack);
	}

	protected ItemStack getParent() {
		for (InteractionHand hand : InteractionHand.values()) {
			ItemStack held = player.getItemInHand(hand);
			if (isSameItemInventory(held, parent)) {
				return held;
			}
		}
		return parent;
	}

	protected void setParent(ItemStack parent) {
		this.parent = parent;
	}

	@Nullable
	protected InteractionHand getHand() {
		for (InteractionHand hand : InteractionHand.values()) {
			ItemStack held = player.getItemInHand(hand);
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

		CompoundTag baseTagCompound = base.getTag();
		CompoundTag comparisonTagCompound = comparison.getTag();
		if (baseTagCompound == null || comparisonTagCompound == null) {
			return false;
		}

		if (!baseTagCompound.contains(KEY_UID) || !comparisonTagCompound.contains(KEY_UID)) {
			return false;
		}

		int baseUID = baseTagCompound.getInt(KEY_UID);
		int comparisonUID = comparisonTagCompound.getInt(KEY_UID);
		return baseUID == comparisonUID;
	}

	private void writeToParentNBT() {
		ItemStack parent = getParent();

		CompoundTag nbt = parent.getTag();
		if (nbt == null) {
			nbt = new CompoundTag();
			parent.setTag(nbt);
		}

		CompoundTag slotsNbt = new CompoundTag();
		for (int i = 0; i < getContainerSize(); i++) {
			ItemStack itemStack = getItem(i);
			if (!itemStack.isEmpty()) {
				String slotKey = getSlotNBTKey(i);
				CompoundTag itemNbt = new CompoundTag();
				itemStack.save(itemNbt);
				slotsNbt.put(slotKey, itemNbt);
			}
		}

		nbt.put(KEY_SLOTS, slotsNbt);
		onWriteNBT(nbt);
	}

	private static String getSlotNBTKey(int i) {
		return Integer.toString(i, Character.MAX_RADIX);
	}

	protected void onWriteNBT(CompoundTag nbt) {
	}

	public void onSlotClick(int slotIndex, Player player) {
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
	public ItemStack removeItem(int index, int count) {
		ItemStack itemstack = ContainerHelper.removeItem(this.inventoryStacks, index, count);

		if (!itemstack.isEmpty()) {
			this.setChanged();
		}

		return itemstack;
	}

	@Override
	public void setItem(int index, ItemStack itemstack) {
		inventoryStacks.set(index, itemstack);

		ItemStack parent = getParent();

		CompoundTag nbt = parent.getTag();
		if (nbt == null) {
			nbt = new CompoundTag();
			parent.setTag(nbt);
		}

		CompoundTag slotNbt;
		if (!nbt.contains(KEY_SLOTS)) {
			slotNbt = new CompoundTag();
			nbt.put(KEY_SLOTS, slotNbt);
		} else {
			slotNbt = nbt.getCompound(KEY_SLOTS);
		}

		String slotKey = getSlotNBTKey(index);

		if (itemstack.isEmpty()) {
			slotNbt.remove(slotKey);
		} else {
			CompoundTag itemNbt = new CompoundTag();
			itemstack.save(itemNbt);

			slotNbt.put(slotKey, itemNbt);
		}
	}

	@Override
	public ItemStack getItem(int i) {
		return inventoryStacks.get(i);
	}

	@Override
	public int getContainerSize() {
		return inventoryStacks.size();
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public final void setChanged() {
		writeToParentNBT();
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public boolean canPlaceItem(int slotIndex, ItemStack itemStack) {
		return canSlotAccept(slotIndex, itemStack);
	}

	@Override
	public void startOpen(Player player) {
	}

	@Override
	public void stopOpen(Player player) {
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack toReturn = getItem(slot);

		if (!toReturn.isEmpty()) {
			setItem(slot, ItemStack.EMPTY);
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
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (capability == ForgeCapabilities.ITEM_HANDLER) {
			return LazyOptional.of(() -> itemHandler).cast();
		}
		return LazyOptional.empty();
	}

	public IItemHandler getItemHandler() {
		return itemHandler;
	}

	@Override
	public void clearContent() {
		this.inventoryStacks.clear();
	}
}
