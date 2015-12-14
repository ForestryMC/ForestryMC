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

import java.io.File;
import java.util.LinkedHashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

import forestry.api.mail.EnumPostage;
import forestry.api.mail.ILetter;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.IPostOffice;
import forestry.api.mail.IPostalCarrier;
import forestry.api.mail.IPostalState;
import forestry.api.mail.IStamps;
import forestry.api.mail.ITradeStation;
import forestry.api.mail.PostManager;
import forestry.mail.items.EnumStampDefinition;
import forestry.plugins.PluginMail;

public class PostOffice extends WorldSavedData implements IPostOffice {

	// / CONSTANTS
	public static final String SAVE_NAME = "ForestryMail";
	private final int[] collectedPostage = new int[EnumPostage.values().length];

	// CONSTRUCTORS
	public PostOffice() {
		super(SAVE_NAME);
	}
	
	public PostOffice(String s) {
		super(s);
	}

	public void setWorld(World world) {
		refreshActiveTradeStations(world);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		for (int i = 0; i < collectedPostage.length; i++) {
			if (nbttagcompound.hasKey("CPS" + i)) {
				collectedPostage[i] = nbttagcompound.getInteger("CPS" + i);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		for (int i = 0; i < collectedPostage.length; i++) {
			nbttagcompound.setInteger("CPS" + i, collectedPostage[i]);
		}
	}

	/* TRADE STATION MANAGMENT */
	private LinkedHashMap<IMailAddress, ITradeStation> activeTradeStations;

	@Override
	public LinkedHashMap<IMailAddress, ITradeStation> getActiveTradeStations(World world) {
		return this.activeTradeStations;
	}

	private void refreshActiveTradeStations(World world) {
		activeTradeStations = new LinkedHashMap<>();
		if (world == null || world.getSaveHandler() == null) {
			return;
		}
		File worldSave = world.getSaveHandler().getMapFileFromName("dummy");
		if (worldSave == null) {
			return;
		}
		File file = worldSave.getParentFile();
		if (!file.exists() || !file.isDirectory()) {
			return;
		}

		for (String str : file.list()) {
			if (!str.startsWith(TradeStation.SAVE_NAME)) {
				continue;
			}
			if (!str.endsWith(".dat")) {
				continue;
			}

			MailAddress address = new MailAddress(str.replace(TradeStation.SAVE_NAME, "").replace(".dat", ""));
			ITradeStation trade = PostManager.postRegistry.getTradeStation(world, address);
			if (trade == null) {
				continue;
			}

			registerTradeStation(trade);
		}
	}

	@Override
	public void registerTradeStation(ITradeStation trade) {
		if (!activeTradeStations.containsKey(trade.getAddress())) {
			activeTradeStations.put(trade.getAddress(), trade);
		}
	}

	@Override
	public void deregisterTradeStation(ITradeStation trade) {
		activeTradeStations.remove(trade.getAddress());
	}

	// / STAMP MANAGMENT
	@Override
	public ItemStack getAnyStamp(int max) {
		return getAnyStamp(EnumPostage.values(), max);
	}

	@Override
	public ItemStack getAnyStamp(EnumPostage postage, int max) {
		return getAnyStamp(new EnumPostage[]{postage}, max);
	}

	@Override
	public ItemStack getAnyStamp(EnumPostage[] postages, int max) {
		for (EnumPostage postage : postages) {
			int collected = Math.min(max, collectedPostage[postage.ordinal()]);
			collectedPostage[postage.ordinal()] -= collected;

			if (collected > 0) {
				EnumStampDefinition stampDefinition = EnumStampDefinition.getFromPostage(postage);
				return PluginMail.items.stamps.get(stampDefinition, collected);
			}
		}

		return null;
	}

	// / DELIVERY
	@Override
	public IPostalState lodgeLetter(World world, ItemStack itemstack, boolean doLodge) {
		ILetter letter = PostManager.postRegistry.getLetter(itemstack);

		if (letter.isProcessed()) {
			return EnumDeliveryState.ALREADY_MAILED;
		}

		if (!letter.isPostPaid()) {
			return EnumDeliveryState.NOT_POSTPAID;
		}

		if (!letter.isMailable()) {
			return EnumDeliveryState.NOT_MAILABLE;
		}

		IPostalState state = EnumDeliveryState.NOT_MAILABLE;
		for (IMailAddress address : letter.getRecipients()) {
			IPostalCarrier carrier = PostManager.postRegistry.getCarrier(address.getType());
			if (carrier == null) {
				continue;
			}
			state = carrier.deliverLetter(world, this, address, itemstack, doLodge);
			if (!state.isOk()) {
				break;
			}
		}

		if (!state.isOk()) {
			return state;
		}

		collectPostage(letter.getPostage());

		markDirty();
		return EnumDeliveryState.OK;

	}

	@Override
	public void collectPostage(ItemStack[] stamps) {
		for (ItemStack stamp : stamps) {
			if (stamp == null) {
				continue;
			}

			if (stamp.getItem() instanceof IStamps) {
				EnumPostage postage = ((IStamps) stamp.getItem()).getPostage(stamp);
				collectedPostage[postage.ordinal()] += stamp.stackSize;
			}
		}
	}
}
