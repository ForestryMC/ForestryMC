/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.mail;

import java.util.ArrayList;
import java.util.Locale;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

import forestry.api.mail.EnumPostage;
import forestry.api.mail.ILetter;
import forestry.api.mail.IPostalState;
import forestry.api.mail.IStamps;
import forestry.api.mail.ITradeStation;
import forestry.api.mail.MailAddress;
import forestry.api.mail.PostManager;
import forestry.api.mail.TradeStationInfo;
import forestry.core.config.ForestryItem;
import forestry.core.utils.InventoryAdapter;
import forestry.core.utils.StackUtils;
import forestry.mail.items.ItemLetter;

public class TradeStation extends WorldSavedData implements ITradeStation, IInventory {

	// / CONSTANTS
	public static final String SAVE_NAME = "TradePO_";
	public static final short SLOT_SIZE = 39;
	public static final short SLOT_TRADEGOOD = 0;
	public static final short SLOT_EXCHANGE_1 = 1;
	public static final short SLOT_EXCHANGE_COUNT = 4;
	public static final short SLOT_LETTERS_1 = 5;
	public static final short SLOT_LETTERS_COUNT = 6;
	public static final short SLOT_STAMPS_1 = 11;
	public static final short SLOT_STAMPS_COUNT = 4;
	public static final short SLOT_INPUTBUF_1 = 15;
	public static final short SLOT_OUTPUTBUF_1 = 27;
	public static final short SLOT_BUFFER_COUNT = 12;
	// / MEMBER
	private String owner;
	private String moniker;
	private boolean isVirtual = false;
	private boolean isInvalid = false;
	private final InventoryAdapter inventory = new InventoryAdapter(SLOT_SIZE, "INV");

	// / CONSTRUCTORS
	public TradeStation(String owner, String moniker, boolean isMoniker) {
		super(SAVE_NAME + moniker);
		this.owner = owner;
		this.moniker = moniker;
	}

	public TradeStation(String savename) {
		super(savename);
	}

	@Override
	public String getMoniker() {
		return this.moniker;
	}

	// / SAVING & LOADING
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		this.owner = nbttagcompound.getString("OWN");
		this.moniker = nbttagcompound.getString("MNK");
		this.isVirtual = nbttagcompound.getBoolean("VRT");
		this.isInvalid = nbttagcompound.getBoolean("IVL");
		inventory.readFromNBT(nbttagcompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		if (this.owner != null && !this.owner.isEmpty())
			nbttagcompound.setString("OWN", this.owner);
		nbttagcompound.setString("MNK", this.moniker);
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
		ItemStack[] condensedRequired = StackUtils.condenseStacks(inventory.getStacks(SLOT_EXCHANGE_1, SLOT_EXCHANGE_COUNT));

		// Set current state
		EnumStationState state = EnumStationState.OK;

		// Virtual trade stations are always ready for service
		if (!isVirtual()) {
			if (!hasPaper())
				state = EnumStationState.INSUFFICIENT_PAPER;
			if (!canPayPostage(2))
				state = EnumStationState.INSUFFICIENT_STAMPS;
			if (countFillableOrders(1, inventory.getStackInSlot(SLOT_TRADEGOOD)) <= 0)
				state = EnumStationState.INSUFFICIENT_TRADE_GOOD;
		}

		return new TradeStationInfo(moniker, owner, inventory.getStackInSlot(SLOT_TRADEGOOD), condensedRequired, state);
	}

	/* ILETTERHANDLER */
	@Override
	public IPostalState handleLetter(World world, String recipient, ItemStack letterstack, boolean doLodge) {

		ILetter letter = PostManager.postRegistry.getLetter(letterstack);

		if (!isVirtual() && !hasPaper())
			return EnumStationState.INSUFFICIENT_PAPER;

		int ordersToFill = StackUtils.containsSets(inventory.getStacks(SLOT_EXCHANGE_1, SLOT_EXCHANGE_COUNT), letter.getAttachments());
		// Not a single match.
		if (ordersToFill <= 0)
			return EnumStationState.INSUFFICIENT_OFFER;

		if (!isVirtual()) {
			int fillable = countFillableOrders(ordersToFill, inventory.getStackInSlot(SLOT_TRADEGOOD));
			// Nothing can be filled.
			if (fillable <= 0)
				return EnumStationState.INSUFFICIENT_TRADE_GOOD;
			if (fillable < ordersToFill)
				ordersToFill = fillable;

			// Check for sufficient output buffer
			int storable = countStorablePayment(ordersToFill, inventory.getStacks(SLOT_EXCHANGE_1, SLOT_EXCHANGE_COUNT));
			if (storable <= 0)
				return EnumStationState.INSUFFICIENT_BUFFER;
			if (storable < ordersToFill)
				ordersToFill = storable;
		}

		// Prepare and send letter
		ILetter mail = new Letter(new MailAddress(this.moniker, EnumAddressee.TRADER.toString().toLowerCase(Locale.ENGLISH)), letter.getSender());
		mail.setText("Please find your order attached.");
		for (int i = 0; i < ordersToFill; i++) {
			mail.addAttachment(inventory.getStackInSlot(SLOT_TRADEGOOD).copy());
		}
		mail.addAttachments(getSurplusAttachments(ordersToFill, letter.getAttachments()));

		// Attach necessary postage
		int requiredPostage = mail.requiredPostage();
		if (!isVirtual())
			if (!canPayPostage(requiredPostage))
				return EnumStationState.INSUFFICIENT_STAMPS;

		int[] stampCount = getPostage(requiredPostage, isVirtual());
		for (int i = 0; i < stampCount.length; i++) {
			if (stampCount[i] > 0)
				mail.addStamps(ForestryItem.stamps.getItemStack(stampCount[i], EnumPostage.values()[i].ordinal()));
		}

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
			for (ItemStack stack : inventory.getStacks(SLOT_EXCHANGE_1, SLOT_EXCHANGE_COUNT)) {
				if (stack == null)
					continue;
				inventory.tryAddStack(stack.copy(), SLOT_OUTPUTBUF_1, SLOT_BUFFER_COUNT, false);
			}
		}

		// Remove resources
		removePaper();
		removeStamps(stampCount);
		removeTradegood(ordersToFill);
		markDirty();

		// Send confirmation message to seller
		if (doLodge && owner != null && !owner.isEmpty()) {
			ILetter confirm = new Letter(new MailAddress(this.moniker, EnumAddressee.TRADER.toString().toLowerCase(Locale.ENGLISH)), new MailAddress(this.owner));
			confirm.setText(ordersToFill + " order(s) from " + letter.getSender().getIdentifier() + " were filled.");
			confirm.addStamps(ForestryItem.stamps.getItemStack(1, EnumPostage.P_1.ordinal()));
			nbttagcompound = new NBTTagCompound();
			confirm.writeToNBT(nbttagcompound);
			ItemStack confirmstack = ForestryItem.letters.getItemStack(1, ItemLetter.encodeMeta(1, ItemLetter.getType(confirm)));
			confirmstack.setTagCompound(nbttagcompound);
			PostManager.postRegistry.getPostOffice(world).lodgeLetter(world, confirmstack, doLodge);
		}

		return EnumDeliveryState.OK;
	}

	/* TRADE LOGIC */
	private int countFillableOrders(int max, ItemStack tradegood) {

		if (tradegood == null)
			return 0;

		// How many orders are fillable?
		int itemCount = 0;
		for (ItemStack stack : inventory.getStacks(SLOT_INPUTBUF_1, SLOT_BUFFER_COUNT)) {
			if (stack != null && stack.isItemEqual(tradegood) && ItemStack.areItemStackTagsEqual(stack, tradegood))
				itemCount += stack.stackSize;
		}

		return (int) Math.floor(itemCount / tradegood.stackSize);
	}

	private int countStorablePayment(int max, ItemStack[] exchange) {

		InventoryAdapter test = inventory.copy();
		int count = 0;

		for (int i = 0; i < max; i++) {
			if (test.tryAddStacksCopy(exchange, true))
				count++;
			else
				break;
		}

		return count;
	}

	private void removeTradegood(int filled) {

		for (int j = 0; j < filled; j++) {
			int toRemove = inventory.getStackInSlot(SLOT_TRADEGOOD).stackSize;
			for (int i = SLOT_INPUTBUF_1; i < SLOT_INPUTBUF_1 + SLOT_BUFFER_COUNT; i++) {
				ItemStack buffer = inventory.getStackInSlot(i);
				if (buffer == null)
					continue;

				if (!buffer.isItemEqual(inventory.getStackInSlot(SLOT_TRADEGOOD)))
					continue;

				if (!ItemStack.areItemStackTagsEqual(buffer, inventory.getStackInSlot(SLOT_TRADEGOOD)))
					continue;

				ItemStack decrease = inventory.decrStackSize(i, toRemove);
				toRemove -= decrease.stackSize;

				if (toRemove <= 0)
					break;
			}
		}
	}

	// Checks if at least one scrap of paper is available
	private boolean hasPaper() {
		for (ItemStack stack : inventory.getStacks(SLOT_LETTERS_1, SLOT_LETTERS_COUNT)) {
			if (stack != null && stack.stackSize > 0)
				return true;
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
		for (ItemStack stamp : inventory.getStacks(SLOT_STAMPS_1, SLOT_STAMPS_COUNT)) {
			if (stamp == null)
				continue;
			if (!(stamp.getItem() instanceof IStamps))
				continue;

			posted += ((IStamps) stamp.getItem()).getPostage(stamp).getValue() * stamp.stackSize;
			if (posted >= postage)
				return true;
		}

		return false;
	}

	private int[] getPostage(int postage, boolean virtual) {
		int[] stamps = new int[EnumPostage.values().length];

		for (int i = EnumPostage.values().length - 1; i > 0; i--) {
			if (postage <= 0)
				break;

			EnumPostage postValue = EnumPostage.values()[i];

			if (postValue.getValue() > postage)
				continue;

			int num = 99;
			if (!virtual)
				num = getNumStamps(postValue);
			int max = (int) Math.floor(postage / postValue.getValue());
			if (max < num)
				num = max;

			stamps[i] = num;
			postage -= num * postValue.getValue();

		}

		return stamps;
	}

	private int getNumStamps(EnumPostage postage) {
		int count = 0;
		for (ItemStack stamp : inventory.getStacks(SLOT_STAMPS_1, SLOT_STAMPS_COUNT)) {
			if (stamp == null)
				continue;
			if (!(stamp.getItem() instanceof IStamps))
				continue;

			if (((IStamps) stamp.getItem()).getPostage(stamp) == postage)
				count += stamp.stackSize;

		}

		return count;
	}

	private void removeStamps(int[] stampCount) {
		for (int i = 1; i < stampCount.length; i++) {

			if (stampCount[i] <= 0)
				continue;

			for (int j = SLOT_STAMPS_1; j < SLOT_STAMPS_1 + SLOT_STAMPS_COUNT; j++) {
				if (stampCount[i] <= 0)
					continue;

				ItemStack stamp = inventory.getStackInSlot(j);
				if (stamp == null)
					continue;
				if (!(stamp.getItem() instanceof IStamps))
					continue;

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
			if (attachments[i] != null)
				pool[i] = attachments[i].copy();
		}

		// Remove stuff until we are only left with the remants
		for (int i = 0; i < filled; i++) {
			ItemStack[] condensedRequired = StackUtils.condenseStacks(inventory.getStacks(SLOT_EXCHANGE_1, SLOT_EXCHANGE_COUNT));
			for (ItemStack req : condensedRequired) {
				for (int j = 0; j < pool.length; j++) {
					ItemStack pol = pool[j];
					if (pol == null)
						continue;
					if (!pol.isItemEqual(req))
						continue;

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
			if (stack != null)
				surplus.add(stack);
		}

		return surplus.toArray(new ItemStack[0]);
	}

	/* IINVENTORY */
	@Override
	public void markDirty() {
		this.markDirty();
		inventory.markDirty();
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
		this.markDirty();
		inventory.setInventorySlotContents(var1, var2);
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
	public String getInventoryName() {
		return inventory.getInventoryName();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return inventory.isUseableByPlayer(var1);
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}
}
