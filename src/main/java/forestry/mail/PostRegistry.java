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
import forestry.api.mail.EnumAddressee;
import forestry.api.mail.ILetter;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.IPostOffice;
import forestry.api.mail.IPostRegistry;
import forestry.api.mail.IPostalCarrier;
import forestry.api.mail.ITradeStation;
import forestry.api.mail.PostManager;
import forestry.core.config.ForestryItem;
import forestry.mail.items.ItemLetter;
import forestry.plugins.PluginMail;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class PostRegistry implements IPostRegistry {

	public static PostOffice cachedPostOffice;
	public static final Map<IMailAddress, POBox> cachedPOBoxes = new HashMap<IMailAddress, POBox>();
	public static final Map<IMailAddress, ITradeStation> cachedTradeStations = new HashMap<IMailAddress, ITradeStation>();

	/**
	 * @param world the Minecraft world the PO box will be in
	 * @param address the potential address of the PO box
	 * @return true if the passed address is valid for PO Boxes.
	 */
	@Override
	public boolean isValidPOBox(World world, IMailAddress address) {
		return address.isPlayer() && address.getName().matches("^[a-zA-Z0-9]+$");
	}

	public static POBox getPOBox(World world, IMailAddress address) {

		if (cachedPOBoxes.containsKey(address))
			return cachedPOBoxes.get(address);

		POBox pobox = (POBox) world.loadItemData(POBox.class, POBox.SAVE_NAME + address);
		if (pobox != null)
			cachedPOBoxes.put(address, pobox);
		return pobox;
	}

	public static POBox getOrCreatePOBox(World world, IMailAddress address) {
		POBox pobox = getPOBox(world, address);

		if (pobox == null) {
			pobox = new POBox(address);
			world.setItemData(POBox.SAVE_NAME + address, pobox);
			pobox.markDirty();
			cachedPOBoxes.put(address, pobox);
			PluginMail.proxy.setPOBoxInfo(world, address, pobox.getPOBoxInfo());
		}

		return pobox;
	}

	/**
	 * @param world the Minecraft world the Trader will be in
	 * @param address the potential address of the Trader
	 * @return true if the passed address can be an address for a trade station
	 */
	@Override
	public boolean isValidTradeAddress(World world, IMailAddress address) {
		return address.isTrader() && address.getName().matches("^[a-zA-Z0-9]+$");
	}

	/**
	 * @param world the Minecraft world the Trader will be in
	 * @param address the potential address of the Trader
	 * @return true if the trade address has not yet been used before.
	 */
	@Override
	public boolean isAvailableTradeAddress(World world, IMailAddress address) {
		return getTradeStation(world, address) == null;
	}

	@Override
	public TradeStation getTradeStation(World world, IMailAddress address) {
		if (cachedTradeStations.containsKey(address))
			return (TradeStation) cachedTradeStations.get(address);

		TradeStation trade = (TradeStation) world.loadItemData(TradeStation.class, TradeStation.SAVE_NAME + address);

		// Only existing and valid mail orders are returned
		if (trade != null && trade.isValid()) {
			cachedTradeStations.put(address, trade);
			getPostOffice(world).registerTradeStation(trade);
			return trade;
		}

		return null;
	}

	@Override
	public TradeStation getOrCreateTradeStation(World world, GameProfile owner, IMailAddress address) {
		TradeStation trade = getTradeStation(world, address);

		if (trade == null) {
			trade = new TradeStation(owner, address);
			world.setItemData(TradeStation.SAVE_NAME + address, trade);
			trade.markDirty();
			cachedTradeStations.put(address, trade);
			getPostOffice(world).registerTradeStation(trade);
		}

		return trade;
	}

	@Override
	public void deleteTradeStation(World world, IMailAddress address) {
		TradeStation trade = getTradeStation(world, address);
		if (trade == null)
			return;

		// Need to be marked as invalid since WorldSavedData seems to do some caching of its own.
		trade.invalidate();
		cachedTradeStations.remove(address);
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

		office.setWorld(world);

		cachedPostOffice = office;
		return office;
	}

	@Override
	public IMailAddress getMailAddress(GameProfile gameProfile) {
		return new MailAddress(gameProfile);
	}

	public IMailAddress getValidMailAddress(World world, GameProfile gameProfile) {
		IMailAddress address = new MailAddress(gameProfile);;
		if (getPOBox(world, address) != null)
			return address;
		return null;
	}

	@Override
	public IMailAddress getMailAddress(String traderName) {
		return new MailAddress(traderName);
	}

	@Override
	public IMailAddress getValidMailAddress(World world, String traderName) {
		IMailAddress address = new MailAddress(traderName);
		if (getTradeStation(world, address) != null)
			return address;
		return null;
	}

	/* CARRIER */
	private final HashMap<EnumAddressee, IPostalCarrier> carriers = new HashMap<EnumAddressee, IPostalCarrier>();

	@Override
	public Map<EnumAddressee, IPostalCarrier> getRegisteredCarriers() {
		return carriers;
	}

	@Override
	public void registerCarrier(IPostalCarrier carrier) {
		carriers.put(carrier.getType(), carrier);
	}

	@Override
	public IPostalCarrier getCarrier(EnumAddressee type) {
		return carriers.get(type);
	}

	/* LETTERS */
	@Override
	public ILetter createLetter(IMailAddress sender, IMailAddress recipient) {
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
