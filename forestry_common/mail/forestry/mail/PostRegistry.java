/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.mail;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.mail.ILetter;
import forestry.api.mail.IPostOffice;
import forestry.api.mail.IPostRegistry;
import forestry.api.mail.IPostalCarrier;
import forestry.api.mail.ITradeStation;
import forestry.api.mail.MailAddress;
import forestry.api.mail.PostManager;
import forestry.core.config.ForestryItem;
import forestry.mail.items.ItemLetter;
import forestry.plugins.PluginMail;

public class PostRegistry implements IPostRegistry {

	public static PostOffice cachedPostOffice;
	public static HashMap<GameProfile, POBox> cachedPOBoxes = new HashMap<GameProfile, POBox>();
	public static HashMap<GameProfile, ITradeStation> cachedTradeStations = new HashMap<GameProfile, ITradeStation>();

	/**
	 * @param world
	 * @param username
	 * @return true if the passed username is valid for poboxes.
	 */
	@Override
	public boolean isValidPOBox(World world, GameProfile username) {
		if (!username.getName().matches("^[a-zA-Z0-9]+$"))
			return false;

		return true;
	}

	public static POBox getPOBox(World world, GameProfile username) {

		if (cachedPOBoxes.containsKey(username))
			return cachedPOBoxes.get(username);

		POBox pobox = (POBox) world.loadItemData(POBox.class, POBox.SAVE_NAME + username.getId());
		if (pobox != null)
			cachedPOBoxes.put(username, pobox);
		return pobox;
	}

	public static POBox getOrCreatePOBox(World world, GameProfile username) {
		POBox pobox = getPOBox(world, username);

		if (pobox == null) {
			pobox = new POBox(username, true);
			world.setItemData(POBox.SAVE_NAME + username.getId(), pobox);
			pobox.markDirty();
			cachedPOBoxes.put(username, pobox);
			PluginMail.proxy.setPOBoxInfo(world, username, pobox.getPOBoxInfo());
		}

		return pobox;
	}

	/**
	 * @param world
	 * @param moniker
	 * @return true if the passed moniker can be a moniker for a trade station
	 */
	@Override
	public boolean isValidTradeMoniker(World world, GameProfile moniker) {
		if (!moniker.getName().matches("^[a-zA-Z0-9]+$"))
			return false;

		return true;
	}

	/**
	 * @param world
	 * @param moniker
	 * @return true if the trade moniker has not yet been used before.
	 */
	@Override
	public boolean isAvailableTradeMoniker(World world, GameProfile moniker) {
		return getTradeStation(world, moniker) == null;
	}

	@Override
	public TradeStation getTradeStation(World world, GameProfile moniker) {
		if (cachedTradeStations.containsKey(moniker))
			return (TradeStation)cachedTradeStations.get(moniker);

		TradeStation trade = (TradeStation) world.loadItemData(TradeStation.class, TradeStation.SAVE_NAME + moniker.getId()+"_"+moniker.getName());

		// Only existing and valid mail orders are returned
		if (trade != null && trade.isValid()) {
			cachedTradeStations.put(moniker, trade);
			getPostOffice(world).registerTradeStation(trade);
			return trade;
		}

		return null;
	}

	@Override
	public TradeStation getOrCreateTradeStation(World world, GameProfile owner, GameProfile moniker) {
		TradeStation trade = getTradeStation(world, moniker);

		if (trade == null) {
			trade = new TradeStation(owner, moniker, true);
			world.setItemData(TradeStation.SAVE_NAME + moniker.getId()+"_"+moniker.getName(), trade);
			trade.markDirty();
			cachedTradeStations.put(moniker, trade);
			getPostOffice(world).registerTradeStation(trade);
		}

		return trade;
	}

	@Override
	public void deleteTradeStation(World world, GameProfile moniker) {
		TradeStation trade = getTradeStation(world, moniker);
		if (trade == null)
			return;

		// Need to be marked as invalid since WorldSavedData seems to do some caching of its own.
		trade.invalidate();
		cachedTradeStations.remove(moniker);
		getPostOffice(world).deregisterTradeStation(trade);
		File file = world.getSaveHandler().getMapFileFromName(trade.mapName);
		file.delete();
	}

	@Override
	public IPostOffice getPostOffice(World world) {
		if (cachedPostOffice != null)
			return cachedPostOffice;

		PostOffice office = (PostOffice) world.loadItemData(PostOffice.class, PostOffice.SAVE_NAME);

		// Create office if there is none yet
		if (office == null) {
			office = new PostOffice();
			world.setItemData(PostOffice.SAVE_NAME, office);
		}

		cachedPostOffice = office;
		return office;
	}

	/* CARRIER */
	private final HashMap<String, IPostalCarrier> carriers = new HashMap<String, IPostalCarrier>();

	@Override
	public Map<String, IPostalCarrier> getRegisteredCarriers() {
		return carriers;
	}

	@Override
	public void registerCarrier(IPostalCarrier carrier) {
		carriers.put(carrier.getUID(), carrier);
	}

	@Override
	public IPostalCarrier getCarrier(String uid) {
		return carriers.get(uid);
	}

	/* LETTERS */
	@Override
	public ILetter createLetter(MailAddress sender, MailAddress recipient) {
		return new Letter(sender, recipient);
	}

	@Override
	public ItemStack createLetterStack(ILetter letter) {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		letter.writeToNBT(nbttagcompound);

		ItemStack mailstack = ForestryItem.letters.getItemStack(1, ItemLetter.encodeMeta(1, ItemLetter.getType(letter)));
		mailstack.setTagCompound(nbttagcompound);

		return mailstack;
	}

	@Override
	public ILetter getLetter(ItemStack itemstack) {
		if (itemstack == null)
			return null;

		if (!PostManager.postRegistry.isLetter(itemstack))
			return null;

		if (itemstack.getTagCompound() == null)
			return null;

		return new Letter(itemstack.getTagCompound());
	}

	@Override
	public boolean isLetter(ItemStack itemstack) {
		return ForestryItem.letters.isItemEqual(itemstack);
	}


}
