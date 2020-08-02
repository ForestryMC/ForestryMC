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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import forestry.core.tiles.IFilterSlotDelegate;

public abstract class ItemInventory implements IInventory, IFilterSlotDelegate, ICapabilityProvider {
    private static final String KEY_SLOTS = "Slots";
    private static final String KEY_UID = "UID";
    private static final Random rand = new Random();

    private final IItemHandler itemHandler = new InvWrapper(this);

    protected final PlayerEntity player;
    private ItemStack parent;    //TODO not final any more. Is this a problem
    private final NonNullList<ItemStack> inventoryStacks;

    public ItemInventory(PlayerEntity player, int size, ItemStack parent) {
        Preconditions.checkArgument(!parent.isEmpty(), "Parent cannot be empty.");

        this.player = player;
        this.parent = parent;
        this.inventoryStacks = NonNullList.withSize(size, ItemStack.EMPTY);

        CompoundNBT nbt = parent.getTag();
        if (nbt == null) {
            nbt = new CompoundNBT();
            parent.setTag(nbt);
        }
        setUID(nbt); // Set a uid to identify the itemStack on SMP

        CompoundNBT nbtSlots = nbt.getCompound(KEY_SLOTS);
        for (int i = 0; i < inventoryStacks.size(); i++) {
            String slotKey = getSlotNBTKey(i);
            if (nbtSlots.contains(slotKey)) {
                CompoundNBT itemNbt = nbtSlots.getCompound(slotKey);
                ItemStack itemStack = ItemStack.read(itemNbt);
                inventoryStacks.set(i, itemStack);
            } else {
                inventoryStacks.set(i, ItemStack.EMPTY);
            }
        }
    }

    public static int getOccupiedSlotCount(ItemStack itemStack) {
        CompoundNBT nbt = itemStack.getTag();
        if (nbt == null) {
            return 0;
        }

        CompoundNBT slotNbt = nbt.getCompound(KEY_SLOTS);
        return slotNbt.size();
    }

    private void setUID(CompoundNBT nbt) {
        if (!nbt.contains(KEY_UID)) {
            nbt.putInt(KEY_UID, rand.nextInt());
        }
    }

    public boolean isParentItemInventory(ItemStack itemStack) {
        ItemStack parent = getParent();
        return isSameItemInventory(parent, itemStack);
    }

    protected ItemStack getParent() {
        for (Hand hand : Hand.values()) {
            ItemStack held = player.getHeldItem(hand);
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
    protected Hand getHand() {
        for (Hand hand : Hand.values()) {
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

        CompoundNBT baseTagCompound = base.getTag();
        CompoundNBT comparisonTagCompound = comparison.getTag();
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

        CompoundNBT nbt = parent.getTag();
        if (nbt == null) {
            nbt = new CompoundNBT();
            parent.setTag(nbt);
        }

        CompoundNBT slotsNbt = new CompoundNBT();
        for (int i = 0; i < getSizeInventory(); i++) {
            ItemStack itemStack = getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                String slotKey = getSlotNBTKey(i);
                CompoundNBT itemNbt = new CompoundNBT();
                itemStack.write(itemNbt);
                slotsNbt.put(slotKey, itemNbt);
            }
        }

        nbt.put(KEY_SLOTS, slotsNbt);
        onWriteNBT(nbt);
    }

    private static String getSlotNBTKey(int i) {
        return Integer.toString(i, Character.MAX_RADIX);
    }

    protected void onWriteNBT(CompoundNBT nbt) {
    }

    public void onSlotClick(int slotIndex, PlayerEntity player) {
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

        CompoundNBT nbt = parent.getTag();
        if (nbt == null) {
            nbt = new CompoundNBT();
            parent.setTag(nbt);
        }

        CompoundNBT slotNbt;
        if (!nbt.contains(KEY_SLOTS)) {
            slotNbt = new CompoundNBT();
            nbt.put(KEY_SLOTS, slotNbt);
        } else {
            slotNbt = nbt.getCompound(KEY_SLOTS);
        }

        String slotKey = getSlotNBTKey(index);

        if (itemstack.isEmpty()) {
            slotNbt.remove(slotKey);
        } else {
            CompoundNBT itemNbt = new CompoundNBT();
            itemstack.write(itemNbt);

            slotNbt.put(slotKey, itemNbt);
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
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public final void markDirty() {
        writeToParentNBT();
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
        return canSlotAccept(slotIndex, itemStack);
    }

    @Override
    public void openInventory(PlayerEntity player) {
    }

    @Override
    public void closeInventory(PlayerEntity player) {
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
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> itemHandler).cast();
        }
        return LazyOptional.empty();
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public void clear() {
        this.inventoryStacks.clear();
    }
}
