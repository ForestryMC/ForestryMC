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
package forestry.mail.gadgets;

import java.util.Collection;
import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;

import forestry.api.core.ForestryAPI;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.IStamps;
import forestry.api.mail.PostManager;
import forestry.core.EnumErrorCode;
import forestry.core.gadgets.TileBase;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.network.GuiId;
import forestry.core.network.PacketPayload;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;
import forestry.mail.MailAddress;
import forestry.mail.TradeStation;
import forestry.mail.triggers.MailTriggers;

import buildcraft.api.statements.ITriggerExternal;

public class MachineTrader extends TileBase {

	private IMailAddress address;

	public MachineTrader() {
		address = new MailAddress();
		setInternalInventory(new TradeStation.TradeStationInventory(TradeStation.SLOT_SIZE, "INV"));
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		if (isLinked()) {
			player.openGui(ForestryAPI.instance, GuiId.TraderGUI.ordinal(), worldObj, pos.getX(), pos.getY(), pos.getZ());
		} else {
			player.openGui(ForestryAPI.instance, GuiId.TraderNameGUI.ordinal(), worldObj, pos.getX(), pos.getY(), pos.getZ());
		}
	}

	@Override
	public void onRemoval() {
		if (isLinked()) {
			PostManager.postRegistry.deleteTradeStation(worldObj, address);
		}
	}

	/* SAVING & LOADING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		if (address != null) {
			NBTTagCompound nbt = new NBTTagCompound();
			address.writeToNBT(nbt);
			nbttagcompound.setTag("address", nbt);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		if (nbttagcompound.hasKey("address")) {
			address = MailAddress.loadFromNBT(nbttagcompound.getCompoundTag("address"));
		}
	}

	@Override
	public PacketPayload getPacketPayload() {
		if (address == null || address.getName() == null) {
			return null;
		}

		PacketPayload payload = new PacketPayload(0, 0, 1);
		payload.stringPayload[0] = address.getName();
		return payload;
	}

	@Override
	public void fromPacketPayload(PacketPayload payload) {
		if (payload.isEmpty()) {
			address = null;
			return;
		}

		String addressName = payload.stringPayload[0];
		address = PostManager.postRegistry.getMailAddress(addressName);
	}

	/* UPDATING */

	/**
	 * The trade station should show errors for missing stamps and paper first.
	 * Once it is able to send letters, it should display other error states.
	 */
	@Override
	public void updateServerSide() {

		if (!isLinked() || !updateOnInterval(10)) {
			return;
		}

		EnumErrorCode errorCode = EnumErrorCode.OK;

		if (!hasPostageMin(3)) {
			errorCode = EnumErrorCode.NOSTAMPS;
		}

		if (!hasPaperMin(2)) {
			if (errorCode == EnumErrorCode.NOSTAMPS) {
				errorCode = EnumErrorCode.NOSTAMPSNOPAPER;
			} else {
				errorCode = EnumErrorCode.NOPAPER;
			}
		}

		if (errorCode != EnumErrorCode.OK) {
			setErrorState(errorCode);
			return;
		}

		IInventory inventory = getInternalInventory();
		ItemStack tradeGood = inventory.getStackInSlot(TradeStation.SLOT_TRADEGOOD);

		if (tradeGood == null) {
			setErrorState(EnumErrorCode.NOTRADE);
			return;
		}

		boolean hasRequest = hasItemCount(TradeStation.SLOT_EXCHANGE_1, TradeStation.SLOT_EXCHANGE_COUNT, null, 1);
		if (!hasRequest) {
			setErrorState(EnumErrorCode.NOTRADE);
			return;
		}

		boolean hasSupplies = hasItemCount(TradeStation.SLOT_SEND_BUFFER, TradeStation.SLOT_SEND_BUFFER_COUNT, tradeGood, tradeGood.stackSize);
		if (!hasSupplies) {
			setErrorState(EnumErrorCode.NOSUPPLIES);
			return;
		}

		if (inventory instanceof TradeStation) {
			if (!((TradeStation) inventory).canReceivePayment()) {
				setErrorState(EnumErrorCode.NOSPACE);
				return;
			}
		}

		setErrorState(EnumErrorCode.OK);
	}

	/* STATE INFORMATION */
	public boolean isLinked() {
		return address.isValid() && getErrorState() != EnumErrorCode.NOTALPHANUMERIC && getErrorState() != EnumErrorCode.NOTUNIQUE;
	}

	/**
	 * Returns true if there are 'itemCount' of 'item' in the inventory
	 * wildcard when item == null, counts all types of items
	 */
	private boolean hasItemCount(int startSlot, int countSlots, ItemStack item, int itemCount) {
		int count = 0;

		IInventory tradeInventory = this.getInternalInventory();
		for (int i = startSlot; i < startSlot + countSlots; i++) {
			ItemStack itemInSlot = tradeInventory.getStackInSlot(i);
			if (itemInSlot == null) {
				continue;
			}
			if (item == null || StackUtils.isIdenticalItem(itemInSlot, item)) {
				count += itemInSlot.stackSize;
			}
			if (count >= itemCount) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns the percentage of the inventory that is occupied by 'item'
	 * if item == null, returns the percentage occupied by all kinds of items
	 */
	private float percentOccupied(int startSlot, int countSlots, ItemStack item) {
		int count = 0;
		int total = 0;

		IInventory tradeInventory = this.getInternalInventory();
		for (int i = startSlot; i < startSlot + countSlots; i++) {
			ItemStack itemInSlot = tradeInventory.getStackInSlot(i);
			if (itemInSlot == null) {
				total += 64;
			} else {
				total += itemInSlot.getMaxStackSize();
				if (item == null || StackUtils.isIdenticalItem(itemInSlot, item)) {
					count += itemInSlot.stackSize;
				}
			}
		}

		return ((float) count / (float) total);
	}

	public boolean hasPaperMin(int count) {
		return hasItemCount(TradeStation.SLOT_LETTERS_1, TradeStation.SLOT_LETTERS_COUNT, new ItemStack(Items.paper), count);
	}

	public boolean hasInputBufMin(float percentage) {
		IInventory inventory = getInternalInventory();
		ItemStack tradeGood = inventory.getStackInSlot(TradeStation.SLOT_TRADEGOOD);
		if (tradeGood == null) {
			return true;
		}
		return percentOccupied(TradeStation.SLOT_SEND_BUFFER, TradeStation.SLOT_SEND_BUFFER_COUNT, tradeGood) > percentage;
	}

	public boolean hasOutputBufMin(float percentage) {
		return percentOccupied(TradeStation.SLOT_RECEIVE_BUFFER, TradeStation.SLOT_RECEIVE_BUFFER_COUNT, null) > percentage;
	}

	public boolean hasPostageMin(int postage) {

		int posted = 0;

		IInventory tradeInventory = this.getInternalInventory();
		for (int i = TradeStation.SLOT_STAMPS_1; i < TradeStation.SLOT_STAMPS_1 + TradeStation.SLOT_STAMPS_COUNT; i++) {
			ItemStack stamp = tradeInventory.getStackInSlot(i);
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

	/* ADDRESS */
	public IMailAddress getAddress() {
		return address;
	}

	public void setAddress(IMailAddress address) {
		if (address == null) {
			throw new NullPointerException("address must not be null");
		}

		if (this.address.isValid() && this.address.equals(address)) {
			return;
		}

		if (Proxies.common.isSimulating(worldObj)) {
			if (!PostManager.postRegistry.isValidTradeAddress(worldObj, address)) {
				setErrorState(EnumErrorCode.NOTALPHANUMERIC);
				return;
			}

			if (!PostManager.postRegistry.isAvailableTradeAddress(worldObj, address)) {
				setErrorState(EnumErrorCode.NOTUNIQUE);
				return;
			}

			this.address = address;
			PostManager.postRegistry.getOrCreateTradeStation(worldObj, getOwnerProfile(), address);
			setErrorState(EnumErrorCode.OK);
		}
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		// Handle client side
		if (!Proxies.common.isSimulating(worldObj) || !address.isValid()) {
			return super.getInternalInventory();
		}

		return (TradeStation) PostManager.postRegistry.getOrCreateTradeStation(worldObj, getOwnerProfile(), address);
	}

	/* ITRIGGERPROVIDER */
	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerExternal> getExternalTriggers(EnumFacing side, TileEntity tile) {
		LinkedList<ITriggerExternal> res = new LinkedList<ITriggerExternal>();
		res.add(MailTriggers.lowPaper64);
		res.add(MailTriggers.lowPaper32);
		res.add(MailTriggers.lowInput25);
		res.add(MailTriggers.lowInput10);
		res.add(MailTriggers.lowPostage40);
		res.add(MailTriggers.lowPostage20);
		res.add(MailTriggers.highBuffer90);
		res.add(MailTriggers.highBuffer75);
		return res;
	}
}
