/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.mail.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import forestry.api.core.IErrorSource;
import forestry.api.core.IErrorState;
import forestry.api.mail.ILetter;
import forestry.core.errors.EnumErrorCode;
import forestry.core.inventory.ItemInventory;
import forestry.core.items.ItemWithGui;
import forestry.core.utils.SlotUtil;
import forestry.mail.Letter;
import forestry.mail.LetterProperties;
import forestry.mail.items.ItemStamp;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class ItemInventoryLetter extends ItemInventory implements IErrorSource {
    private final ILetter letter;

    public ItemInventoryLetter(PlayerEntity player, ItemStack itemstack) {
        super(player, 0, itemstack);
        CompoundNBT tagCompound = itemstack.getTag();
        Preconditions.checkNotNull(tagCompound);
        letter = new Letter(tagCompound);
    }

    public ILetter getLetter() {
        return letter;
    }

    public void onLetterClosed() {
        ItemStack parent = getParent();
        setParent(LetterProperties.closeLetter(parent, letter));
    }

    public void onLetterOpened() {
        ItemStack parent = getParent();
        setParent(LetterProperties.openLetter(parent));
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack result = letter.decrStackSize(index, count);
        CompoundNBT tagCompound = getParent().getTag();
        Preconditions.checkNotNull(tagCompound);
        letter.write(tagCompound);
        return result;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack itemstack) {
        letter.setInventorySlotContents(index, itemstack);
        CompoundNBT tagCompound = getParent().getTag();
        Preconditions.checkNotNull(tagCompound);
        letter.write(tagCompound);
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return letter.getStackInSlot(i);
    }

    @Override
    public int getSizeInventory() {
        return letter.getSizeInventory();
    }

    @Override
    public int getInventoryStackLimit() {
        return letter.getInventoryStackLimit();
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return letter.isUsableByPlayer(player);
    }

    @Override
    public ItemStack removeStackFromSlot(int slot) {
        return letter.removeStackFromSlot(slot);
    }

    @Override
    public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
        if (letter.isProcessed()) {
            return false;
        } else if (SlotUtil.isSlotInRange(slotIndex, Letter.SLOT_POSTAGE_1, Letter.SLOT_POSTAGE_COUNT)) {
            Item item = itemStack.getItem();
            return item instanceof ItemStamp;
        } else if (SlotUtil.isSlotInRange(slotIndex, Letter.SLOT_ATTACHMENT_1, Letter.SLOT_ATTACHMENT_COUNT)) {
            return !(itemStack.getItem() instanceof ItemWithGui);
        }
        return false;
    }

    /* IErrorSource */
    @Override
    public ImmutableSet<IErrorState> getErrorStates() {

        ImmutableSet.Builder<IErrorState> errorStates = ImmutableSet.builder();

        if (!letter.hasRecipient()) {
            errorStates.add(EnumErrorCode.NO_RECIPIENT);
        }

        if (!letter.isProcessed() && !letter.isPostPaid()) {
            errorStates.add(EnumErrorCode.NOT_POST_PAID);
        }

        return errorStates.build();
    }
}
