/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.mail;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

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
	public static HashMap<String, POBox> cachedPOBoxes = new HashMap<String, POBox>();
	public static HashMap<String, ITradeStation> cachedTradeStations = new HashMap<String, ITradeStation>();

	/**
	 * @param world
	 * @param username
	 * @return true if the passed username is valid for poboxes.
	 */
	@Override
	public boolean isValidPOBox(World world, String username) {
		if (!username.matches("^[a-zA-Z0-9]+$"))
			return false;

		return true;
	}

	public static POBox getPOBox(World world, String username) {

		if (cachedPOBoxes.containsKey(username.toLowerCase(Locale.ENGLISH)))
			return cachedPOBoxes.get(username.toLowerCase(Locale.ENGLISH));

		POBox pobox = (POBox) world.loadItemData(POBox.class, POBox.SAVE_NAME + username.toLowerCase(Locale.ENGLISH));
		if (pobox != null)
			cachedPOBoxes.put(username.toLowerCase(Locale.ENGLISH), pobox);
		return pobox;
	}

	public static POBox getOrCreatePOBox(World world, String username) {
		POBox pobox = getPOBox(world, username);

		if (pobox == null) {
			pobox = new POBox(username.toLowerCase(Locale.ENGLISH), true);
			world.setItemData(POBox.SAVE_NAME + username.toLowerCase(Locale.ENGLISH), pobox);
			pobox.markDirty();
			cachedPOBoxes.put(username.toLowerCase(Locale.ENGLISH), pobox);
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
	public boolean isValidTradeMoniker(World world, String moniker) {
		if (!moniker.matches("^[a-zA-Z0-9]+$"))
			return false;

		return true;
	}

	/**
	 * @param world
	 * @param moniker
	 * @return true if the trade moniker has not yet been used before.
	 */
	@Override
	public boolean isAvailableTradeMoniker(World world, String moniker) {
		return getTradeStation(world, moniker) == null;
	}

	@Override
	public TradeStation getTradeStation(World world, String moniker) {
		if (cachedTradeStations.containsKey(moniker))
			return (TradeStation)cachedTradeStations.get(moniker);

		TradeStation trade = (TradeStation) world.loadItemData(TradeStation.class, TradeStation.SAVE_NAME + moniker);

		// Only existing and valid mail orders are returned
		if (trade != null && trade.isValid()) {
			cachedTradeStations.put(moniker, trade);
			getPostOffice(world).registerTradeStation(trade);
			return trade;
		}

		return null;
	}

	@Override
	public TradeStation getOrCreateTradeStation(World world, String owner, String moniker) {
		TradeStation trade = getTradeStation(world, moniker);

		if (trade == null) {
			trade = new TradeStation(owner, moniker, true);
			world.setItemData(TradeStation.SAVE_NAME + moniker, trade);
			trade.markDirty();
			cachedTradeStations.put(moniker, trade);
			getPostOffice(world).registerTradeStation(trade);
		}

		return trade;
	}

	@Override
	public void deleteTradeStation(World world, String moniker) {
		TradeStation trade = (TradeStation)getTradeStation(world, moniker);
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
	private HashMap<String, IPostalCarrier> carriers = new HashMap<String, IPostalCarrier>();

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
