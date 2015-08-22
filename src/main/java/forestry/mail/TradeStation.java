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

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

import com.mojang.authlib.GameProfile;

import forestry.api.mail.EnumPostage;
import forestry.api.mail.ILetter;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.IPostalState;
import forestry.api.mail.IStamps;
import forestry.api.mail.ITradeStation;
import forestry.api.mail.PostManager;
import forestry.api.mail.TradeStationInfo;
import forestry.core.config.ForestryItem;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.InvTools;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.utils.GuiUtil;
import forestry.core.utils.StackUtils;
import forestry.mail.items.ItemLetter;

public class TradeStation extends WorldSavedData implements ITradeStation, IInventoryAdapter {

	public static class TradeStationInventory extends InventoryAdapter {

		public TradeStationInventory(int size, String name) {
			super(size, name);
		}
		
		@Override
		public int[] getSlotsForFace(EnumFacing side) {

			ArrayList<Integer> slots = new ArrayList<Integer>();

			for (int i = SLOT_LETTERS_1; i < SLOT_LETTERS_1 + SLOT_LETTERS_COUNT; i++) {
				slots.add(i);
			}
			for (int i = SLOT_STAMPS_1; i < SLOT_STAMPS_1 + SLOT_STAMPS_COUNT; i++) {
				slots.add(i);
			}
			for (int i = SLOT_SEND_BUFFER; i < SLOT_SEND_BUFFER + SLOT_SEND_BUFFER_COUNT; i++) {
				slots.add(i);
			}

			int[] slotsInt = new int[slots.size()];
			for (int i = 0; i < slots.size(); i++) {
				slotsInt[i] = slots.get(i);
			}

			return slotsInt;
		}

		@Override
		public boolean canExtractItem(int slot, ItemStack itemStack, EnumFacing side) {
			return GuiUtil.isIndexInRange(slot, SLOT_RECEIVE_BUFFER, SLOT_RECEIVE_BUFFER_COUNT);
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			if (GuiUtil.isIndexInRange(slotIndex, SLOT_SEND_BUFFER, SLOT_SEND_BUFFER_COUNT)) {
				for (int i = 0; i < SLOT_TRADEGOOD_COUNT; i++) {
					ItemStack tradeGood = getStackInSlot(SLOT_TRADEGOOD + i);
					if (StackUtils.isIdenticalItem(tradeGood, itemStack)) {
						return true;
					}
				}
				return false;
			} else if (GuiUtil.isIndexInRange(slotIndex, SLOT_LETTERS_1, SLOT_LETTERS_COUNT)) {
				Item item = itemStack.getItem();
				return item == Items.paper;
			} else if (GuiUtil.isIndexInRange(slotIndex, SLOT_STAMPS_1, SLOT_STAMPS_COUNT)) {
				Item item = itemStack.getItem();
				return item instanceof IStamps;
			}

			return false;
		}
	}

	// / CONSTANTS
	public static final String SAVE_NAME = "TradePO_";
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
	public static final short SLOT_SIZE = SLOT_TRADEGOOD_COUNT + SLOT_EXCHANGE_COUNT + SLOT_LETTERS_COUNT + SLOT_STAMPS_COUNT + SLOT_RECEIVE_BUFFER_COUNT + SLOT_SEND_BUFFER_COUNT;

	// / MEMBER
	private GameProfile owner;
	private IMailAddress address;
	private boolean isVirtual = false;
	private boolean isInvalid = false;
	private final InventoryAdapter inventory = new TradeStationInventory(SLOT_SIZE, "INV");

	// / CONSTRUCTORS
	public TradeStation(GameProfile owner, IMailAddress address) {
		super(SAVE_NAME + address);
		if (!address.isTrader()) {
			throw new IllegalArgumentException("TradeStation address must be a trader");
		}
		this.owner = owner;
		this.address = address;
	}

	public TradeStation(String savename) {
		super(savename);
	}

	@Override
	public IMailAddress getAddress() {
		return this.address;
	}

	// / SAVING & LOADING
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		if (nbttagcompound.hasKey("owner")) {
			owner = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("owner"));
		}

		if (nbttagcompound.hasKey("address")) {
			address = MailAddress.loadFromNBT(nbttagcompound.getCompoundTag("address"));
		}

		this.isVirtual = nbttagcompound.getBoolean("VRT");
		this.isInvalid = nbttagcompound.getBoolean("IVL");
		inventory.readFromNBT(nbttagcompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		if (owner != null) {
			NBTTagCompound nbt = new NBTTagCompound();
			NBTUtil.writeGameProfile(nbt, owner);
			nbttagcompound.setTag("owner", nbt);
		}

		if (address != null) {
			NBTTagCompound nbt = new NBTTagCompound();
			address.writeToNBT(nbt);
			nbttagcompound.setTag("address", nbt);
		}

		nbttagcompound.setBoolean("VRT", this.isVirtual);
		nbttagcompound.setBoolean("IVL", this.isInvalid);
		inventory.writeToNBT(nbttagcompound);
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
		ItemStack[] condensedRequired = StackUtils.condenseStacks(InvTools.getStacks(inventory, SLOT_EXCHANGE_1, SLOT_EXCHANGE_COUNT));

		// Set current state
		EnumStationState state = EnumStationState.OK;

		// Virtual trade stations are always ready for service.
		if (!isVirtual()) {
			// Will assume that the owner should get a notice.
			if (!hasPaper(2)) {
				state = EnumStationState.INSUFFICIENT_PAPER;
			}

			if (!canPayPostage(3)) {
				state = EnumStationState.INSUFFICIENT_STAMPS;
			}

			if (countFillableOrders(1, inventory.getStackInSlot(SLOT_TRADEGOOD)) <= 0) {
				state = EnumStationState.INSUFFICIENT_TRADE_GOOD;
			}
		}

		return new TradeStationInfo(address, owner, inventory.getStackInSlot(SLOT_TRADEGOOD), condensedRequired, state);
	}

	/* ILETTERHANDLER */
	@Override
	public IPostalState handleLetter(World world, IMailAddress recipient, ItemStack letterstack, boolean doLodge) {
		
		boolean sendOwnerNotice = doLodge && owner != null;
		
		ILetter letter = PostManager.postRegistry.getLetter(letterstack);

		if (!isVirtual() && !hasPaper(sendOwnerNotice ? 2 : 1)) {
			return EnumStationState.INSUFFICIENT_PAPER;
		}

		int ordersToFill = StackUtils.containsSets(InvTools.getStacks(inventory, SLOT_EXCHANGE_1, SLOT_EXCHANGE_COUNT), letter.getAttachments());

		// Not a single match.
		if (ordersToFill <= 0) {
			return EnumStationState.INSUFFICIENT_OFFER;
		}

		if (!isVirtual()) {
			int fillable = countFillableOrders(ordersToFill, inventory.getStackInSlot(SLOT_TRADEGOOD));

			// Nothing can be filled.
			if (fillable <= 0) {
				return EnumStationState.INSUFFICIENT_TRADE_GOOD;
			}

			if (fillable < ordersToFill) {
				ordersToFill = fillable;
			}

			// Check for sufficient output buffer
			int storable = countStorablePayment(ordersToFill, InvTools.getStacks(inventory, SLOT_EXCHANGE_1, SLOT_EXCHANGE_COUNT));

			if (storable <= 0) {
				return EnumStationState.INSUFFICIENT_BUFFER;
			}

			if (storable < ordersToFill) {
				ordersToFill = storable;
			}
		}

		// Prepare the letter
		ILetter mail = new Letter(this.address, letter.getSender());
		mail.setText("Please find your order attached.");
		for (int i = 0; i < ordersToFill; i++) {
			mail.addAttachment(inventory.getStackInSlot(SLOT_TRADEGOOD).copy());
		}
		mail.addAttachments(getSurplusAttachments(ordersToFill, letter.getAttachments()));

		// Check for necessary postage
		int requiredPostage = mail.requiredPostage();
		if (!isVirtual()) {
			if (!canPayPostage(requiredPostage + (sendOwnerNotice ? 1 : 0))) {
				return EnumStationState.INSUFFICIENT_STAMPS;
			}
		}

		// Attach necessary postage
		int[] stampCount = getPostage(requiredPostage, isVirtual());
		for (int i = 0; i < stampCount.length; i++) {
			if (stampCount[i] > 0) {
				mail.addStamps(ForestryItem.stamps.getItemStack(stampCount[i], EnumPostage.values()[i].ordinal() - 1));
			}
		}

		// Send the letter
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		mail.writeToNBT(nbttagcompound);

		ItemStack mailstack = ForestryItem.letters.getItemStack(1, ItemLetter.encodeMeta(1, ItemLetter.getType(mail)));
		mailstack.setTagCompound(nbttagcompound);

		IPostalState responseState = PostManager.postRegistry.getPostOffice(world).lodgeLetter(world, mailstack, doLodge);
		
		if (!responseState.isOk()) {
			return EnumDeliveryState.RESPONSE_NOT_MAILABLE;
		}

		// Store received items
		for (int i = 0; i < ordersToFill; i++) {
			for (ItemStack stack : InvTools.getStacks(inventory, SLOT_EXCHANGE_1, SLOT_EXCHANGE_COUNT)) {
				if (stack == null) {
					continue;
				}

				InvTools.tryAddStack(inventory, stack.copy(), SLOT_RECEIVE_BUFFER, SLOT_RECEIVE_BUFFER_COUNT, false);
			}
		}

		// Remove resources
		removePaper();
		removeStamps(stampCount);
		removeTradegood(ordersToFill);

		// Send confirmation message to seller
		if (sendOwnerNotice) {
			nbttagcompound = new NBTTagCompound();

			ILetter confirm = new Letter(this.address, new MailAddress(this.owner));
			confirm.setText(ordersToFill + " order(s) from " + letter.getSender().getName() + " were filled.");
			confirm.addStamps(ForestryItem.stamps.getItemStack(1, EnumPostage.P_1.ordinal() - 1));
			confirm.writeToNBT(nbttagcompound);

			ItemStack confirmstack = ForestryItem.letters.getItemStack(1, ItemLetter.encodeMeta(1, ItemLetter.getType(confirm)));
			confirmstack.setTagCompound(nbttagcompound);

			PostManager.postRegistry.getPostOffice(world).lodgeLetter(world, confirmstack, doLodge);
			
			removePaper();
			removeStamps(new int[]{0, 1});
		}
		
		markDirty();

		return EnumDeliveryState.OK;
	}

	/* TRADE LOGIC */
	private int countFillableOrders(int max, ItemStack tradegood) {

		if (tradegood == null) {
			return 0;
		}

		// How many orders are fillable?
		int itemCount = 0;

		for (ItemStack stack : InvTools.getStacks(inventory, SLOT_SEND_BUFFER, SLOT_SEND_BUFFER_COUNT)) {
			if (stack != null && stack.isItemEqual(tradegood) && ItemStack.areItemStackTagsEqual(stack, tradegood)) {
				itemCount += stack.stackSize;
			}
		}

		return (int) Math.floor(itemCount / tradegood.stackSize);
	}

	public boolean canReceivePayment() {
		InventoryAdapter test = inventory.copy();
		ItemStack[] payment = InvTools.getStacks(inventory, SLOT_EXCHANGE_1, SLOT_EXCHANGE_COUNT);

		return InvTools.tryAddStacksCopy(test, payment, SLOT_RECEIVE_BUFFER, SLOT_RECEIVE_BUFFER_COUNT, true);
	}

	private int countStorablePayment(int max, ItemStack[] exchange) {
		
		InventoryAdapter test = inventory.copy();
		int count = 0;

		for (int i = 0; i < max; i++) {
			if (InvTools.tryAddStacksCopy(test, exchange, SLOT_RECEIVE_BUFFER, SLOT_RECEIVE_BUFFER_COUNT, true)) {
				count++;
			} else {
				break;
			}
		}

		return count;
	}

	private void removeTradegood(int filled) {

		for (int j = 0; j < filled; j++) {
			int toRemove = inventory.getStackInSlot(SLOT_TRADEGOOD).stackSize;
			for (int i = SLOT_SEND_BUFFER; i < SLOT_SEND_BUFFER + SLOT_SEND_BUFFER_COUNT; i++) {
				ItemStack buffer = inventory.getStackInSlot(i);
				if (buffer == null) {
					continue;
				}

				if (!buffer.isItemEqual(inventory.getStackInSlot(SLOT_TRADEGOOD))) {
					continue;
				}

				if (!ItemStack.areItemStackTagsEqual(buffer, inventory.getStackInSlot(SLOT_TRADEGOOD))) {
					continue;
				}

				ItemStack decrease = inventory.decrStackSize(i, toRemove);
				toRemove -= decrease.stackSize;

				if (toRemove <= 0) {
					break;
				}
			}
		}
	}

	// Checks if this trade station has enough paper.
	private boolean hasPaper(int amountRequired) {
		
		int amountFound = 0;

		for (ItemStack stack : InvTools.getStacks(inventory, SLOT_LETTERS_1, SLOT_LETTERS_COUNT)) {
			if (stack != null) {
				amountFound += stack.stackSize;
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
			if (stack != null && stack.getItem() == Items.paper && stack.stackSize > 0) {
				inventory.decrStackSize(i, 1);
				break;
			}
		}
	}

	private boolean canPayPostage(int postage) {
		int posted = 0;

		for (ItemStack stamp : InvTools.getStacks(inventory, SLOT_STAMPS_1, SLOT_STAMPS_COUNT)) {
			if (stamp == null) {
				continue;
			}

			if (!(stamp.getItem() instanceof IStamps)) {
				continue;
			}

			posted += ((IStamps) stamp.getItem()).getPostage(stamp).getValue() * stamp.stackSize;

			if (posted >= postage) {
				return true;
			}
		}

		return false;
	}

	private int[] getPostage(int postage, boolean virtual) {
		int[] stamps = new int[EnumPostage.values().length];

		for (int i = EnumPostage.values().length - 1; i > 0; i--) {
			if (postage <= 0) {
				break;
			}

			EnumPostage postValue = EnumPostage.values()[i];

			if (postValue.getValue() > postage) {
				continue;
			}

			int num = 99;
			if (!virtual) {
				num = getNumStamps(postValue);
			}
			int max = (int) Math.floor(postage / postValue.getValue());
			if (max < num) {
				num = max;
			}

			stamps[i] = num;
			postage -= num * postValue.getValue();
		}

		return stamps;
	}

	private int getNumStamps(EnumPostage postage) {
		int count = 0;
		for (ItemStack stamp : InvTools.getStacks(inventory, SLOT_STAMPS_1, SLOT_STAMPS_COUNT)) {
			if (stamp == null) {
				continue;
			}
			if (!(stamp.getItem() instanceof IStamps)) {
				continue;
			}

			if (((IStamps) stamp.getItem()).getPostage(stamp) == postage) {
				count += stamp.stackSize;
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
				if (stamp == null) {
					continue;
				}

				if (!(stamp.getItem() instanceof IStamps)) {
					continue;
				}

				if (((IStamps) stamp.getItem()).getPostage(stamp) == EnumPostage.values()[i]) {
					ItemStack decrease = inventory.decrStackSize(j, stampCount[i]);
					stampCount[i] -= decrease.stackSize;
				}
			}
		}
	}

	private ItemStack[] getSurplusAttachments(int filled, ItemStack[] attachments) {
		ArrayList<ItemStack> surplus = new ArrayList<ItemStack>();

		// Get a copy of the attachments to play with
		ItemStack[] pool = new ItemStack[attachments.length];
		for (int i = 0; i < attachments.length; i++) {
			if (attachments[i] != null) {
				pool[i] = attachments[i].copy();
			}
		}

		// Remove stuff until we are only left with the remnants
		for (int i = 0; i < filled; i++) {
			ItemStack[] required = InvTools.getStacks(inventory, SLOT_EXCHANGE_1, SLOT_EXCHANGE_COUNT);
			ItemStack[] condensedRequired = StackUtils.condenseStacks(required);
			for (ItemStack req : condensedRequired) {
				for (int j = 0; j < pool.length; j++) {
					ItemStack pol = pool[j];
					if (pol == null) {
						continue;
					}
					if (!pol.isItemEqual(req)) {
						continue;
					}

					if (req.stackSize >= pol.stackSize) {
						req.stackSize -= pol.stackSize;
						pool[j] = null;
					} else {
						pol.stackSize -= req.stackSize;
						req.stackSize = 0;
					}
				}
			}
		}

		for (ItemStack stack : pool) {
			if (stack != null) {
				surplus.add(stack);
			}
		}

		return surplus.toArray(new ItemStack[surplus.size()]);
	}

	/* IINVENTORY */
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
	public ItemStack getStackInSlotOnClosing(int var1) {
		return inventory.getStackInSlotOnClosing(var1);
	}
	
	@Override
	public String getCommandSenderName() {
		return inventory.getCommandSenderName();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemStack) {
		return inventory.isItemValidForSlot(i, itemStack);
	}

	@Override
	public boolean hasCustomName() {
		return true;
	}
	
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return inventory.getSlotsForFace(side);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemStack, EnumFacing side) {
		return inventory.canInsertItem(slot, itemStack, side);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemStack, EnumFacing side) {
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
	public IInventoryAdapter configureSided(int[] sides, int[] slots) {
		return inventory.configureSided(sides, slots);
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

	@Override
	public IChatComponent getDisplayName() {
		return null;
	}
}
