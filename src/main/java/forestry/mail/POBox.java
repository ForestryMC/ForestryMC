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
package forestry.mail;

import com.google.common.base.Preconditions;
import forestry.api.mail.EnumAddressee;
import forestry.api.mail.ILetter;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.PostManager;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.utils.InventoryUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nullable;

public class POBox extends WorldSavedData implements IInventory {

    public static final String SAVE_NAME = "pobox_";
    public static final short SLOT_SIZE = 84;
    private final InventoryAdapter letters = new InventoryAdapter(SLOT_SIZE, "Letters").disableAutomation();
    @Nullable
    private IMailAddress address;

    public POBox(IMailAddress address) {
        super(SAVE_NAME + address);
        if (address.getType() != EnumAddressee.PLAYER) {
            throw new IllegalArgumentException("POBox address must be a player");
        }
        this.address = address;
    }

    @SuppressWarnings("unused")
    public POBox(String savename) {
        super(savename);
    }

    @Override
    public void read(CompoundNBT compoundNBT) {
        if (compoundNBT.contains("address")) {
            this.address = new MailAddress(compoundNBT.getCompound("address"));
        }
        letters.read(compoundNBT);
    }

    @Override
    public CompoundNBT write(CompoundNBT compoundNBT) {
        if (this.address != null) {
            CompoundNBT nbt = new CompoundNBT();
            this.address.write(nbt);
            compoundNBT.put("address", nbt);
        }
        letters.write(compoundNBT);
        return compoundNBT;
    }

    public boolean storeLetter(ItemStack letterstack) {
        ILetter letter = PostManager.postRegistry.getLetter(letterstack);
        Preconditions.checkNotNull(letter, "Letter stack must be a valid letter");

        // Mark letter as processed
        letter.setProcessed(true);
        letter.invalidatePostage();
        CompoundNBT compoundNBT = new CompoundNBT();
        letter.write(compoundNBT);
        letterstack.setTag(compoundNBT);

        this.markDirty();

        return InventoryUtil.tryAddStack(letters, letterstack, true);
    }

    public POBoxInfo getPOBoxInfo() {
        int playerLetters = 0;
        int tradeLetters = 0;
        for (int i = 0; i < letters.getSizeInventory(); i++) {
            if (letters.getStackInSlot(i).isEmpty()) {
                continue;
            }
            CompoundNBT tagCompound = letters.getStackInSlot(i).getTag();
            if (tagCompound != null) {
                ILetter letter = new Letter(tagCompound);
                if (letter.getSender().getType() == EnumAddressee.PLAYER) {
                    playerLetters++;
                } else {
                    tradeLetters++;
                }
            }
        }

        return new POBoxInfo(playerLetters, tradeLetters);
    }

    /* IINVENTORY */

    @Override
    public boolean isEmpty() {
        return letters.isEmpty();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        letters.markDirty();
    }

    @Override
    public void setInventorySlotContents(int var1, ItemStack var2) {
        this.markDirty();
        letters.setInventorySlotContents(var1, var2);
    }

    @Override
    public int getSizeInventory() {
        return letters.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int var1) {
        return letters.getStackInSlot(var1);
    }

    @Override
    public ItemStack decrStackSize(int var1, int var2) {
        return letters.decrStackSize(var1, var2);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return letters.removeStackFromSlot(index);
    }

    //	@Override
    //	public String getName() {
    //		return letters.getName();
    //	}

    @Override
    public int getInventoryStackLimit() {
        return letters.getInventoryStackLimit();
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity var1) {
        return letters.isUsableByPlayer(var1);
    }

    @Override
    public void openInventory(PlayerEntity var1) {
    }

    @Override
    public void closeInventory(PlayerEntity var1) {
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return letters.isItemValidForSlot(i, itemstack);
    }

    @Override
    public void clear() {
    }

}
