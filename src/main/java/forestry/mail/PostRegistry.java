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

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import com.mojang.authlib.GameProfile;

import forestry.api.mail.EnumAddressee;
import forestry.api.mail.ILetter;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.IPostOffice;
import forestry.api.mail.IPostRegistry;
import forestry.api.mail.IPostalCarrier;
import forestry.api.mail.ITradeStation;
import forestry.api.mail.PostManager;
import forestry.core.utils.Log;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.PlayerUtil;
import forestry.mail.features.MailItems;
import forestry.mail.network.packets.PacketPOBoxInfoResponse;

public class PostRegistry implements IPostRegistry {
	@Nullable
	public static PostOffice cachedPostOffice;
	public static final Map<IMailAddress, POBox> cachedPOBoxes = new HashMap<>();
	public static final Map<IMailAddress, ITradeStation> cachedTradeStations = new HashMap<>();

	private final Map<EnumAddressee, IPostalCarrier> carriers = new EnumMap<>(EnumAddressee.class);

	/**
	 * @param world   the Minecraft world the PO box will be in
	 * @param address the potential address of the PO box
	 * @return true if the passed address is valid for PO Boxes.
	 */
	@Override
	public boolean isValidPOBox(Level world, IMailAddress address) {
		return address.getType() == EnumAddressee.PLAYER && address.getName().matches("^[a-zA-Z0-9]+$");
	}

	@Nullable
	public static POBox getPOBox(ServerLevel world, IMailAddress address) {

		if (cachedPOBoxes.containsKey(address)) {
			return cachedPOBoxes.get(address);
		}

		//TODO  needs getOrCreate
		POBox pobox = world.getDataStorage().get(() -> new POBox(address), POBox.SAVE_NAME + address);
		if (pobox != null) {
			cachedPOBoxes.put(address, pobox);
		}
		return pobox;
	}

	public static POBox getOrCreatePOBox(ServerLevel world, IMailAddress add) {
		POBox pobox = getPOBox(world, add);

		if (pobox == null) {
			pobox = world.getDataStorage().computeIfAbsent(() -> new POBox(add), POBox.SAVE_NAME + add);
			pobox.setDirty();
			cachedPOBoxes.put(add, pobox);

			Player player = PlayerUtil.getPlayer(world, add.getPlayerProfile());
			if (player != null) {
				NetworkUtil.sendToPlayer(new PacketPOBoxInfoResponse(pobox.getPOBoxInfo()), player);
			}
		}

		return pobox;
	}

	/**
	 * @param world   the Minecraft world the Trader will be in
	 * @param address the potential address of the Trader
	 * @return true if the passed address can be an address for a trade station
	 */
	@Override
	public boolean isValidTradeAddress(Level world, IMailAddress address) {
		return address.getType() == EnumAddressee.TRADER && address.getName().matches("^[a-zA-Z0-9]+$");
	}

	/**
	 * @param world   the Minecraft world the Trader will be in
	 * @param address the potential address of the Trader
	 * @return true if the trade address has not yet been used before.
	 */
	@Override
	public boolean isAvailableTradeAddress(ServerLevel world, IMailAddress address) {
		return getTradeStation(world, address) == null;
	}

	@Override
	public TradeStation getTradeStation(ServerLevel world, IMailAddress address) {
		if (cachedTradeStations.containsKey(address)) {
			return (TradeStation) cachedTradeStations.get(address);
		}

		//TODO again this should be altered to use getOrCreate. At the moment this may supply bad trade stations with no owner. Not sure how this code will handle that
		TradeStation trade = world.getDataStorage().get(() -> new TradeStation(TradeStation.SAVE_NAME + address), TradeStation.SAVE_NAME + address);

		// Only existing and valid mail orders are returned
		if (trade != null && trade.isValid()) {
			cachedTradeStations.put(address, trade);
			getPostOffice(world).registerTradeStation(trade);
			return trade;
		}

		return null;
	}

	@Override
	public TradeStation getOrCreateTradeStation(ServerLevel world, GameProfile owner, IMailAddress address) {
		TradeStation trade = getTradeStation(world, address);

		if (trade == null) {
			trade = world.getDataStorage().computeIfAbsent(() -> new TradeStation(owner, address), TradeStation.SAVE_NAME + address);
			trade.setDirty();
			cachedTradeStations.put(address, trade);
			getPostOffice(world).registerTradeStation(trade);
		}

		return trade;
	}

	@Override
	public void deleteTradeStation(ServerLevel world, IMailAddress address) {
		TradeStation trade = getTradeStation(world, address);
		if (trade == null) {
			return;
		}

		// Need to be marked as invalid since WorldSavedData seems to do some caching of its own.
		trade.invalidate();
		cachedTradeStations.remove(address);
		getPostOffice(world).deregisterTradeStation(trade);
		//		File file = world.getSaveHandler().getMapFileFromName(trade.getName());	//TODO which file?
		boolean delete = false; //TODO fix file.delete();
		if (!delete) {
			Log.error("Failed to delete trade station file. {}", "FIXME!");//file);
		}
	}

	@Override
	public IPostOffice getPostOffice(ServerLevel world) {
		if (cachedPostOffice != null) {
			return cachedPostOffice;
		}

		PostOffice office = world.getDataStorage().computeIfAbsent(PostOffice::new, PostOffice.SAVE_NAME);

		office.setWorld(world);

		cachedPostOffice = office;
		return office;
	}


	@Override
	public IMailAddress getMailAddress(GameProfile gameProfile) {
		return new MailAddress(gameProfile);
	}

	@Override
	public IMailAddress getMailAddress(String traderName) {
		return new MailAddress(traderName);
	}

	/* CARRIER */
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
		CompoundTag compoundNBT = new CompoundTag();
		letter.write(compoundNBT);

		ItemStack letterStack = LetterProperties.createStampedLetterStack(letter);
		letterStack.setTag(compoundNBT);

		return letterStack;
	}

	@Override
	@Nullable
	public ILetter getLetter(ItemStack itemstack) {
		if (itemstack.isEmpty()) {
			return null;
		}

		if (!PostManager.postRegistry.isLetter(itemstack)) {
			return null;
		}

		if (itemstack.getTag() == null) {
			return null;
		}

		return new Letter(itemstack.getTag());
	}

	@Override
	public boolean isLetter(ItemStack itemstack) {
		return MailItems.LETTERS.itemEqual(itemstack);
	}
}
