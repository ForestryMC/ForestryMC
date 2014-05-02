/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.mail;

import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IPostRegistry {
	
	/* POST OFFICE */
	IPostOffice getPostOffice(World world);

	/* LETTERS */
	boolean isLetter(ItemStack itemstack);

	ILetter createLetter(MailAddress sender, MailAddress recipient);
	
	ILetter getLetter(ItemStack itemstack);
	
	ItemStack createLetterStack(ILetter letter);
	
	/* CARRIERS */
	/**
	 * Registers a new {@link IPostalCarrier}. See {@link IPostalCarrier} for details.
	 * @param carrier {@link IPostalCarrier} to register.
	 */
	void registerCarrier(IPostalCarrier carrier);
	
	IPostalCarrier getCarrier(String uid);

	Map<String, IPostalCarrier> getRegisteredCarriers();

	/* TRADE STATIONS */
	void deleteTradeStation(World world, String moniker);

	ITradeStation getOrCreateTradeStation(World world, String owner, String moniker);

	ITradeStation getTradeStation(World world, String moniker);

	boolean isAvailableTradeMoniker(World world, String moniker);

	boolean isValidTradeMoniker(World world, String moniker);

	/* PO BOXES */
	boolean isValidPOBox(World world, String username);

}
