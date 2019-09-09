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
package forestry.mail.tiles;

import com.google.common.base.Preconditions;

import java.io.IOException;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.IErrorLogic;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.IStamps;
import forestry.api.mail.PostManager;
import forestry.core.errors.EnumErrorCode;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.network.PacketBufferForestry;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.owner.OwnerHandler;
import forestry.core.tiles.TileBase;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.NetworkUtil;
import forestry.mail.MailAddress;
import forestry.mail.ModuleMail;
import forestry.mail.TradeStation;
import forestry.mail.gui.ContainerTradeName;
import forestry.mail.gui.ContainerTrader;
import forestry.mail.inventory.InventoryTradeStation;
import forestry.mail.network.packets.PacketTraderAddressResponse;

public class TileTrader extends TileBase implements IOwnedTile {
	private final OwnerHandler ownerHandler = new OwnerHandler();
	private IMailAddress address;

	public TileTrader() {
		super(ModuleMail.getTiles().TRADER);
		address = new MailAddress();
		setInternalInventory(new InventoryTradeStation());
	}

	@Override
	public IOwnerHandler getOwnerHandler() {
		return ownerHandler;
	}

	@Override
	public void onRemoval() {
		if (isLinked() && !world.isRemote) {
			PostManager.postRegistry.deleteTradeStation((ServerWorld) world, address);
		}
	}

	/* SAVING & LOADING */
	@Override
	public CompoundNBT write(CompoundNBT compoundNBT) {
		compoundNBT = super.write(compoundNBT);

		CompoundNBT nbt = new CompoundNBT();
		address.write(nbt);
		compoundNBT.put("address", nbt);

		ownerHandler.write(compoundNBT);
		return compoundNBT;
	}

	@Override
	public void read(CompoundNBT compoundNBT) {
		super.read(compoundNBT);

		if (compoundNBT.contains("address")) {
			address = new MailAddress(compoundNBT.getCompound("address"));
		}
		ownerHandler.read(compoundNBT);
	}

	/* NETWORK */

	@Override
	public void writeData(PacketBufferForestry data) {
		super.writeData(data);
		ownerHandler.writeData(data);
		String addressName = address.getName();
		data.writeString(addressName);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void readData(PacketBufferForestry data) throws IOException {
		super.readData(data);
		ownerHandler.readData(data);
		String addressName = data.readString();
		if (!addressName.isEmpty()) {
			address = PostManager.postRegistry.getMailAddress(addressName);
		}
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

		IErrorLogic errorLogic = getErrorLogic();

		errorLogic.setCondition(!hasPostageMin(3), EnumErrorCode.NO_STAMPS);
		errorLogic.setCondition(!hasPaperMin(2), EnumErrorCode.NO_PAPER);

		IInventory inventory = getInternalInventory();
		ItemStack tradeGood = inventory.getStackInSlot(TradeStation.SLOT_TRADEGOOD);
		errorLogic.setCondition(tradeGood.isEmpty(), EnumErrorCode.NO_TRADE);

		boolean hasRequest = hasItemCount(TradeStation.SLOT_EXCHANGE_1, TradeStation.SLOT_EXCHANGE_COUNT, ItemStack.EMPTY, 1);
		errorLogic.setCondition(!hasRequest, EnumErrorCode.NO_TRADE);

		if (!tradeGood.isEmpty()) {
			boolean hasSupplies = hasItemCount(TradeStation.SLOT_SEND_BUFFER, TradeStation.SLOT_SEND_BUFFER_COUNT, tradeGood, tradeGood.getCount());
			errorLogic.setCondition(!hasSupplies, EnumErrorCode.NO_SUPPLIES);
		}

		if (inventory instanceof TradeStation && updateOnInterval(200)) {
			boolean canReceivePayment = ((TradeStation) inventory).canReceivePayment();
			errorLogic.setCondition(!canReceivePayment, EnumErrorCode.NO_SPACE_INVENTORY);
		}
	}

	/* STATE INFORMATION */
	public boolean isLinked() {
		if (!address.isValid()) {
			return false;
		}

		IErrorLogic errorLogic = getErrorLogic();

		return !errorLogic.contains(EnumErrorCode.NOT_ALPHANUMERIC) && !errorLogic.contains(EnumErrorCode.NOT_UNIQUE);
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
			if (itemInSlot.isEmpty()) {
				continue;
			}
			if (item.isEmpty() || ItemStackUtil.isIdenticalItem(itemInSlot, item)) {
				count += itemInSlot.getCount();
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
			if (itemInSlot.isEmpty()) {
				total += tradeInventory.getInventoryStackLimit();
			} else {
				total += itemInSlot.getMaxStackSize();
				if (item.isEmpty() || ItemStackUtil.isIdenticalItem(itemInSlot, item)) {
					count += itemInSlot.getCount();
				}
			}
		}

		return (float) count / (float) total;
	}

	public boolean hasPaperMin(int count) {
		return hasItemCount(TradeStation.SLOT_LETTERS_1, TradeStation.SLOT_LETTERS_COUNT, new ItemStack(Items.PAPER), count);
	}

	//	public boolean hasInputBufMin(float percentage) {
	//		IInventory inventory = getInternalInventory();
	//		ItemStack tradeGood = inventory.getStackInSlot(TradeStation.SLOT_TRADEGOOD);
	//		if (tradeGood.isEmpty()) {
	//			return true;
	//		}
	//		return percentOccupied(TradeStation.SLOT_SEND_BUFFER, TradeStation.SLOT_SEND_BUFFER_COUNT, tradeGood) > percentage;
	//	}

	//	public boolean hasOutputBufMin(float percentage) {
	//		return percentOccupied(TradeStation.SLOT_RECEIVE_BUFFER, TradeStation.SLOT_RECEIVE_BUFFER_COUNT, ItemStack.EMPTY) > percentage;
	//	}

	public boolean hasPostageMin(int postage) {

		int posted = 0;

		IInventory tradeInventory = this.getInternalInventory();
		for (int i = TradeStation.SLOT_STAMPS_1; i < TradeStation.SLOT_STAMPS_1 + TradeStation.SLOT_STAMPS_COUNT; i++) {
			ItemStack stamp = tradeInventory.getStackInSlot(i);
			if (!stamp.isEmpty()) {
				if (stamp.getItem() instanceof IStamps) {
					posted += ((IStamps) stamp.getItem()).getPostage(stamp).getValue() * stamp.getCount();
					if (posted >= postage) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/* ADDRESS */
	public IMailAddress getAddress() {
		return address;
	}

	public void handleSetAddressRequest(String addressName) {
		IMailAddress address = PostManager.postRegistry.getMailAddress(addressName);
		setAddress(address);

		IMailAddress newAddress = getAddress();
		String newAddressName = newAddress.getName();
		if (newAddressName.equals(addressName)) {
			PacketTraderAddressResponse packetResponse = new PacketTraderAddressResponse(this, addressName);
			NetworkUtil.sendNetworkPacket(packetResponse, pos, world);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void handleSetAddressResponse(String addressName) {
		IMailAddress address = PostManager.postRegistry.getMailAddress(addressName);
		setAddress(address);
	}

	private void setAddress(IMailAddress address) {
		Preconditions.checkNotNull(address, "address must not be null");

		if (this.address.isValid() && this.address.equals(address)) {
			return;
		}

		if (!world.isRemote) {
			ServerWorld world = (ServerWorld) this.world;
			IErrorLogic errorLogic = getErrorLogic();

			boolean hasValidTradeAddress = PostManager.postRegistry.isValidTradeAddress(world, address);
			errorLogic.setCondition(!hasValidTradeAddress, EnumErrorCode.NOT_ALPHANUMERIC);

			boolean hasUniqueTradeAddress = PostManager.postRegistry.isAvailableTradeAddress(world, address);
			errorLogic.setCondition(!hasUniqueTradeAddress, EnumErrorCode.NOT_UNIQUE);

			if (hasValidTradeAddress & hasUniqueTradeAddress) {
				this.address = address;
				PostManager.postRegistry.getOrCreateTradeStation(world, getOwnerHandler().getOwner(), address);
			}
		} else {
			this.address = address;
		}
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		// Handle client side
		if (world.isRemote || !address.isValid()) {
			return super.getInternalInventory();
		}

		return (TradeStation) PostManager.postRegistry.getOrCreateTradeStation((ServerWorld) world, getOwnerHandler().getOwner(), address);
	}

	//	@Optional.Method(modid = Constants.BCLIB_MOD_ID)
	//	@Override
	//	public void addExternalTriggers(Collection<ITriggerExternal> triggers, @Nonnull Direction side, TileEntity tile) {
	//		super.addExternalTriggers(triggers, side, tile);
	//		triggers.add(MailTriggers.lowPaper64);
	//		triggers.add(MailTriggers.lowPaper32);
	//		triggers.add(MailTriggers.lowInput25);
	//		triggers.add(MailTriggers.lowInput10);
	//		triggers.add(MailTriggers.lowPostage40);
	//		triggers.add(MailTriggers.lowPostage20);
	//		triggers.add(MailTriggers.highBuffer90);
	//		triggers.add(MailTriggers.highBuffer75);
	//	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
		if (isLinked()) {    //TODO does this sync over?
			return new ContainerTrader(windowId, inv, this);
		} else {
			return new ContainerTradeName(windowId, inv, this);
		}
	}
}
