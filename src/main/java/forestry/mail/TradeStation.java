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
package forestry.mail;

import com.mojang.authlib.GameProfile;
import forestry.api.mail.*;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.mail.features.MailItems;
import forestry.mail.inventory.InventoryTradeStation;
import forestry.mail.items.EnumStampDefinition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public class TradeStation extends WorldSavedData implements ITradeStation, IInventoryAdapter {
    public static final String SAVE_NAME = "trade_po_";
    public static final short SLOT_TRADEGOOD = 0;
    public static final short SLOT_TRADEGOOD_COUNT = 1;
    public static final short SLOT_EXCHANGE_1 = 1;
    public static final short SLOT_EXCHANGE_COUNT = 4;
    public static final short SLOT_LETTERS_1 = 5;
    public static final short SLOT_LETTERS_COUNT = 6;
    public static final short SLOT_STAMPS_1 = 11;
    public static final short SLOT_STAMPS_COUNT = 4;
    public static final short SLOT_RECEIVE_BUFFER = 15;
    public static final short SLOT_RECEIVE_BUFFER_COUNT = 15;
    public static final short SLOT_SEND_BUFFER = 30;
    public static final short SLOT_SEND_BUFFER_COUNT = 10;
    public static final int SLOT_SIZE = SLOT_TRADEGOOD_COUNT + SLOT_EXCHANGE_COUNT + SLOT_LETTERS_COUNT + SLOT_STAMPS_COUNT + SLOT_RECEIVE_BUFFER_COUNT + SLOT_SEND_BUFFER_COUNT;

    private GameProfile owner;
    private IMailAddress address;
    private boolean isVirtual = false;
    private boolean isInvalid = false;
    private final InventoryAdapter inventory = new InventoryTradeStation();

    public TradeStation(GameProfile owner, IMailAddress address) {
        super(SAVE_NAME + address);
        if (address.getType() != EnumAddressee.TRADER) {
            throw new IllegalArgumentException("TradeStation address must be a trader");
        }
        this.owner = owner;
        this.address = address;
    }

    @SuppressWarnings("unused") // required for WorldSavedData
    public TradeStation(String savename) {
        super(savename);
    }

    @Override
    public IMailAddress getAddress() {
        return this.address;
    }

    // / SAVING & LOADING
    @Override
    public void read(CompoundNBT compoundNBT) {
        if (compoundNBT.contains("owner")) {
            owner = NBTUtil.readGameProfile(compoundNBT.getCompound("owner"));
        }

        if (compoundNBT.contains("address")) {
            address = new MailAddress(compoundNBT.getCompound("address"));
        }

        this.isVirtual = compoundNBT.getBoolean("VRT");
        this.isInvalid = compoundNBT.getBoolean("IVL");
        inventory.read(compoundNBT);
    }

    @Override
    public CompoundNBT write(CompoundNBT compoundNBT) {
        if (owner != null) {
            CompoundNBT nbt = new CompoundNBT();
            NBTUtil.writeGameProfile(nbt, owner);
            compoundNBT.put("owner", nbt);
        }

        if (address != null) {
            CompoundNBT nbt = new CompoundNBT();
            address.write(nbt);
            compoundNBT.put("address", nbt);
        }

        compoundNBT.putBoolean("VRT", this.isVirtual);
        compoundNBT.putBoolean("IVL", this.isInvalid);
        inventory.write(compoundNBT);
        return compoundNBT;
    }

    /* INVALIDATING */
    @Override
    public boolean isValid() {
        return !this.isInvalid;
    }

    @Override
    public void invalidate() {
        this.isInvalid = true;
    }

    /* INFORMATION */
    @Override
    public void setVirtual(boolean isVirtual) {
        this.isVirtual = isVirtual;
        markDirty();
    }

    @Override
    public boolean isVirtual() {
        return isVirtual;
    }

    @Override
    public TradeStationInfo getTradeInfo() {
        NonNullList<ItemStack> condensedRequired = ItemStackUtil.condenseStacks(InventoryUtil.getStacks(
                inventory,
                SLOT_EXCHANGE_1,
                SLOT_EXCHANGE_COUNT
        ));

        // Set current state
        EnumTradeStationState state = EnumTradeStationState.OK;

        // Virtual trade stations are always ready for service.
        if (!isVirtual()) {
            // Will assume that the owner should get a notice.
            if (!hasPaper(2)) {
                state = EnumTradeStationState.INSUFFICIENT_PAPER;
            }

            if (!canPayPostage(3)) {
                state = EnumTradeStationState.INSUFFICIENT_STAMPS;
            }

            if (countFillableOrders(1, inventory.getStackInSlot(SLOT_TRADEGOOD)) <= 0) {
                state = EnumTradeStationState.INSUFFICIENT_TRADE_GOOD;
            }
        }

        return new TradeStationInfo(address, owner, inventory.getStackInSlot(SLOT_TRADEGOOD), condensedRequired, state);
    }

    /* ILETTERHANDLER */
    //TODO this method is long. Shorten it.
    @Override
    public IPostalState handleLetter(
            ServerWorld world,
            IMailAddress recipient,
            ItemStack letterstack,
            boolean doLodge
    ) {

        boolean sendOwnerNotice = doLodge && owner != null;

        ILetter letter = PostManager.postRegistry.getLetter(letterstack);

        if (!isVirtual() && !hasPaper(sendOwnerNotice ? 2 : 1)) {
            return EnumTradeStationState.INSUFFICIENT_PAPER;
        }

        int ordersToFillCount = ItemStackUtil.containsSets(InventoryUtil.getStacks(
                inventory,
                SLOT_EXCHANGE_1,
                SLOT_EXCHANGE_COUNT
        ), letter.getAttachments());

        // Not a single match.
        if (ordersToFillCount <= 0) {
            return EnumTradeStationState.INSUFFICIENT_OFFER;
        }

        if (!isVirtual()) {
            int fillable = countFillableOrders(ordersToFillCount, inventory.getStackInSlot(SLOT_TRADEGOOD));

            // Nothing can be filled.
            if (fillable <= 0) {
                return EnumTradeStationState.INSUFFICIENT_TRADE_GOOD;
            }

            if (fillable < ordersToFillCount) {
                ordersToFillCount = fillable;
            }

            // Check for sufficient output buffer
            int storable = countStorablePayment(
                    ordersToFillCount,
                    InventoryUtil.getStacks(inventory, SLOT_EXCHANGE_1, SLOT_EXCHANGE_COUNT)
            );

            if (storable <= 0) {
                return EnumTradeStationState.INSUFFICIENT_BUFFER;
            }

            if (storable < ordersToFillCount) {
                ordersToFillCount = storable;
            }
        }

        // Prepare the letter
        ILetter mail = new Letter(this.address, letter.getSender());
        mail.setText(new TranslationTextComponent("for.gui.mail.order.attached").getString());
        for (int i = 0; i < ordersToFillCount; i++) {
            mail.addAttachment(inventory.getStackInSlot(SLOT_TRADEGOOD).copy());
        }
        mail.addAttachments(getSurplusAttachments(ordersToFillCount, letter.getAttachments()));

        // Check for necessary postage
        int requiredPostage = mail.requiredPostage();
        if (!isVirtual()) {
            if (!canPayPostage(requiredPostage + (sendOwnerNotice ? 1 : 0))) {
                return EnumTradeStationState.INSUFFICIENT_STAMPS;
            }
        }

        // Attach necessary postage
        int[] stampCount = getPostage(requiredPostage, isVirtual());
        for (int i = 0; i < stampCount.length; i++) {
            int count = stampCount[i];
            if (count > 0) {
                EnumPostage postage = EnumPostage.values()[i];
                EnumStampDefinition stampDefinition = EnumStampDefinition.getFromPostage(postage);
                mail.addStamps(MailItems.STAMPS.stack(stampDefinition, count));
            }
        }

        // Send the letter
        CompoundNBT compoundNBT = new CompoundNBT();
        mail.write(compoundNBT);

        ItemStack mailstack = LetterProperties.createStampedLetterStack(mail);
        mailstack.setTag(compoundNBT);

        IPostalState responseState = PostManager.postRegistry.getPostOffice(world).lodgeLetter(
                world,
                mailstack,
                doLodge
        );

        if (!responseState.isOk()) {
            return new ResponseNotMailable(responseState);
        }

        // Store received items
        for (int i = 0; i < ordersToFillCount; i++) {
            for (ItemStack stack : InventoryUtil.getStacks(inventory, SLOT_EXCHANGE_1, SLOT_EXCHANGE_COUNT)) {
                if (stack == null) {
                    continue;
                }

                InventoryUtil.tryAddStack(
                        inventory,
                        stack.copy(),
                        SLOT_RECEIVE_BUFFER,
                        SLOT_RECEIVE_BUFFER_COUNT,
                        false
                );
            }
        }

        // Remove resources
        removePaper();
        removeStamps(stampCount);
        removeTradegood(ordersToFillCount);

        // Send confirmation message to seller
        if (sendOwnerNotice) {
            compoundNBT = new CompoundNBT();

            ILetter confirm = new Letter(this.address, new MailAddress(this.owner));

            String orderFilledMessage;
            if (ordersToFillCount == 1) {
                orderFilledMessage = new TranslationTextComponent("for.gui.mail.order.filled.one").getString();
            } else {
                orderFilledMessage = new TranslationTextComponent("for.gui.mail.order.filled.multiple").getString();
                orderFilledMessage = orderFilledMessage.replace("%COUNT", Integer.toString(ordersToFillCount));
            }

            orderFilledMessage = orderFilledMessage.replace("%SENDER", letter.getSender().getName());

            confirm.setText(orderFilledMessage);
            confirm.addStamps(MailItems.STAMPS.stack(EnumStampDefinition.P_1, 1));
            confirm.write(compoundNBT);

            ItemStack confirmstack = LetterProperties.createStampedLetterStack(confirm);
            confirmstack.setTag(compoundNBT);

            PostManager.postRegistry.getPostOffice(world).lodgeLetter(world, confirmstack, doLodge);

            removePaper();
            removeStamps(new int[]{0, 1});
        }

        markDirty();

        return EnumDeliveryState.OK;
    }

    /* TRADE LOGIC */
    private int countFillableOrders(int max, ItemStack tradegood) {

        if (tradegood.isEmpty()) {
            return 0;
        }

        // How many orders are fillable?
        float orderCount = 0;

        for (ItemStack stack : InventoryUtil.getStacks(inventory, SLOT_SEND_BUFFER, SLOT_SEND_BUFFER_COUNT)) {
            if (stack != null && stack.isItemEqual(tradegood) && ItemStack.areItemStackTagsEqual(stack, tradegood)) {
                orderCount += stack.getCount() / (float) tradegood.getCount();
                if (orderCount >= max) {
                    return max;
                }
            }
        }

        return (int) Math.floor(orderCount);
    }

    public boolean canReceivePayment() {
        InventoryAdapter test = inventory.copy();
        NonNullList<ItemStack> payment = InventoryUtil.getStacks(inventory, SLOT_EXCHANGE_1, SLOT_EXCHANGE_COUNT);

        return InventoryUtil.tryAddStacksCopy(test, payment, SLOT_RECEIVE_BUFFER, SLOT_RECEIVE_BUFFER_COUNT, true);
    }

    private int countStorablePayment(int max, NonNullList<ItemStack> exchange) {

        InventoryAdapter test = inventory.copy();
        int count = 0;

        for (int i = 0; i < max; i++) {
            if (InventoryUtil.tryAddStacksCopy(test, exchange, SLOT_RECEIVE_BUFFER, SLOT_RECEIVE_BUFFER_COUNT, true)) {
                count++;
            } else {
                break;
            }
        }

        return count;
    }

    private void removeTradegood(int filled) {

        for (int j = 0; j < filled; j++) {
            int toRemove = inventory.getStackInSlot(SLOT_TRADEGOOD).getCount();
            for (int i = SLOT_SEND_BUFFER; i < SLOT_SEND_BUFFER + SLOT_SEND_BUFFER_COUNT; i++) {
                ItemStack buffer = inventory.getStackInSlot(i);

                if (!buffer.isEmpty() &&
                        buffer.isItemEqual(inventory.getStackInSlot(SLOT_TRADEGOOD)) &&
                        ItemStack.areItemStackTagsEqual(buffer, inventory.getStackInSlot(SLOT_TRADEGOOD))) {

                    ItemStack decrease = inventory.decrStackSize(i, toRemove);
                    toRemove -= decrease.getCount();

                    if (toRemove <= 0) {
                        break;
                    }
                }
            }
        }
    }

    // Checks if this trade station has enough paper.
    private boolean hasPaper(int amountRequired) {

        int amountFound = 0;

        for (ItemStack stack : InventoryUtil.getStacks(inventory, SLOT_LETTERS_1, SLOT_LETTERS_COUNT)) {
            if (stack != null) {
                amountFound += stack.getCount();
            }

            if (amountFound >= amountRequired) {
                return true;
            }
        }

        return false;
    }

    // Removes a single paper from the inventory
    private void removePaper() {
        for (int i = SLOT_LETTERS_1; i < SLOT_LETTERS_1 + SLOT_LETTERS_COUNT; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.getItem() == Items.PAPER) {
                inventory.decrStackSize(i, 1);
                break;
            }
        }
    }

    private boolean canPayPostage(int postage) {
        int posted = 0;

        for (ItemStack stamp : InventoryUtil.getStacks(inventory, SLOT_STAMPS_1, SLOT_STAMPS_COUNT)) {
            if (stamp == null) {
                continue;
            }

            if (!(stamp.getItem() instanceof IStamps)) {
                continue;
            }

            posted += ((IStamps) stamp.getItem()).getPostage(stamp).getValue() * stamp.getCount();

            if (posted >= postage) {
                return true;
            }
        }

        return false;
    }

    private int[] getPostage(final int postageRequired, boolean virtual) {
        int[] stamps = new int[EnumPostage.values().length];
        int postageRemaining = postageRequired;

        for (int i = EnumPostage.values().length - 1; i > 0; i--) {
            if (postageRemaining <= 0) {
                break;
            }

            EnumPostage postValue = EnumPostage.values()[i];

            if (postValue.getValue() > postageRemaining) {
                continue;
            }

            int num = virtual ? 99 : getNumStamps(postValue);
            int max = (int) Math.floor(postageRemaining / postValue.getValue());
            if (max < num) {
                num = max;
            }

            stamps[i] = num;
            postageRemaining -= num * postValue.getValue();
        }

        // use larger stamps if exact change isn't available
        if (postageRemaining > 0) {
            for (int i = 0; i < EnumPostage.values().length; i++) {
                EnumPostage postValue = EnumPostage.values()[i];

                if (postValue.getValue() >= postageRequired) {
                    stamps = new int[EnumPostage.values().length];

                    int num = virtual ? 99 : getNumStamps(postValue);
                    if (num > 0) {
                        stamps[i] = 1;
                        return stamps;
                    }
                }
            }
        }

        return stamps;
    }

    private int getNumStamps(EnumPostage postage) {
        int count = 0;
        for (ItemStack stamp : InventoryUtil.getStacks(inventory, SLOT_STAMPS_1, SLOT_STAMPS_COUNT)) {
            if (stamp == null) {
                continue;
            }
            if (!(stamp.getItem() instanceof IStamps)) {
                continue;
            }

            if (((IStamps) stamp.getItem()).getPostage(stamp) == postage) {
                count += stamp.getCount();
            }

        }

        return count;
    }

    private void removeStamps(int[] stampCount) {
        for (int i = 1; i < stampCount.length; i++) {

            if (stampCount[i] <= 0) {
                continue;
            }

            for (int j = SLOT_STAMPS_1; j < SLOT_STAMPS_1 + SLOT_STAMPS_COUNT; j++) {
                if (stampCount[i] <= 0) {
                    continue;
                }

                ItemStack stamp = inventory.getStackInSlot(j);
                if (stamp.isEmpty()) {
                    continue;
                }

                if (!(stamp.getItem() instanceof IStamps)) {
                    continue;
                }

                if (((IStamps) stamp.getItem()).getPostage(stamp) == EnumPostage.values()[i]) {
                    ItemStack decrease = inventory.decrStackSize(j, stampCount[i]);
                    stampCount[i] -= decrease.getCount();
                }
            }
        }
    }

    private NonNullList<ItemStack> getSurplusAttachments(int filled, NonNullList<ItemStack> attachments) {
        NonNullList<ItemStack> surplus = NonNullList.create();

        // Get a copy of the attachments to play with
        NonNullList<ItemStack> pool = NonNullList.withSize(attachments.size(), ItemStack.EMPTY);
        for (int i = 0; i < attachments.size(); i++) {
            ItemStack attachment = attachments.get(i);
            if (!attachment.isEmpty()) {
                pool.set(i, attachment.copy());
            }
        }

        // Remove stuff until we are only left with the remnants
        for (int i = 0; i < filled; i++) {
            NonNullList<ItemStack> required = InventoryUtil.getStacks(inventory, SLOT_EXCHANGE_1, SLOT_EXCHANGE_COUNT);
            NonNullList<ItemStack> condensedRequired = ItemStackUtil.condenseStacks(required);
            for (ItemStack req : condensedRequired) {
                for (int j = 0; j < pool.size(); j++) {
                    ItemStack pol = pool.get(j);
                    if (pol.isEmpty()) {
                        continue;
                    }
                    if (!pol.isItemEqual(req)) {
                        continue;
                    }

                    if (req.getCount() >= pol.getCount()) {
                        req.shrink(pol.getCount());
                        pool.set(j, ItemStack.EMPTY);
                    } else {
                        pol.shrink(req.getCount());
                        req.setCount(0);
                    }
                }
            }
        }

        for (ItemStack stack : pool) {
            if (stack != null) {
                surplus.add(stack);
            }
        }

        return surplus;
    }

    /* IINVENTORY */

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        inventory.markDirty();
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack) {
        this.markDirty();
        inventory.setInventorySlotContents(slot, itemStack);
    }

    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int var1) {
        return inventory.getStackInSlot(var1);
    }

    @Override
    public ItemStack decrStackSize(int var1, int var2) {
        return inventory.decrStackSize(var1, var2);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return inventory.removeStackFromSlot(index);
    }

    //tODO
    //	@Override
    //	public String getName() {
    //		return inventory.getName();
    //	}

    @Override
    public int getInventoryStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return true;
    }

    @Override
    public void openInventory(PlayerEntity player) {
    }

    @Override
    public void closeInventory(PlayerEntity player) {
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemStack) {
        return inventory.isItemValidForSlot(i, itemStack);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return inventory.getSlotsForFace(side);
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemStack, Direction side) {
        return inventory.canInsertItem(slot, itemStack, side);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemStack, Direction side) {
        return inventory.canExtractItem(slot, itemStack, side);
    }

    @Override
    public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
        return inventory.canSlotAccept(slotIndex, itemStack);
    }

    @Override
    public boolean isLocked(int slotIndex) {
        return inventory.isLocked(slotIndex);
    }

    @Override
    public void clear() {
    }

}
