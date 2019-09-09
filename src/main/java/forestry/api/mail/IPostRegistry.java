/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.mail;

import javax.annotation.Nullable;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import com.mojang.authlib.GameProfile;

public interface IPostRegistry {

	/* POST OFFICE */
	IPostOffice getPostOffice(ServerWorld world);

	/* MAIL ADDRESSES */
	IMailAddress getMailAddress(GameProfile gameProfile);

	IMailAddress getMailAddress(String traderName);

	/* LETTERS */
	boolean isLetter(ItemStack itemstack);

	ILetter createLetter(IMailAddress sender, IMailAddress recipient);

	@Nullable
	ILetter getLetter(ItemStack itemstack);

	ItemStack createLetterStack(ILetter letter);

	/* CARRIERS */

	/**
	 * Registers a new {@link IPostalCarrier}. See {@link IPostalCarrier} for details.
	 *
	 * @param carrier {@link IPostalCarrier} to register.
	 */
	void registerCarrier(IPostalCarrier carrier);

	@Nullable
	IPostalCarrier getCarrier(EnumAddressee uid);

	Map<EnumAddressee, IPostalCarrier> getRegisteredCarriers();

	/* TRADE STATIONS */
	void deleteTradeStation(ServerWorld world, IMailAddress address);

	ITradeStation getOrCreateTradeStation(ServerWorld world, GameProfile owner, IMailAddress address);

	@Nullable
	ITradeStation getTradeStation(ServerWorld world, IMailAddress address);

	boolean isAvailableTradeAddress(ServerWorld world, IMailAddress address);

	boolean isValidTradeAddress(World world, IMailAddress address);

	/* PO BOXES */
	boolean isValidPOBox(World world, IMailAddress address);

}
