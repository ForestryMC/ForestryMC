/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.item.ItemStack;

public interface IGameMode {

	/**
	 * @return Human-readable identifier for the game mode. (i.e. 'EASY', 'NORMAL', 'HARD')
	 */
	String getIdentifier();

	/**
	 * @param ident Identifier for the setting. (See the gamemode config.)
	 * @return Value of the requested setting, false if unknown setting.
	 */
	boolean getBooleanSetting(String ident);
	
	/**
	 * @param ident Identifier for the setting. (See the gamemode config.)
	 * @return Value of the requested setting, 0 if unknown setting.
	 */
	int getIntegerSetting(String ident);

	/**
	 * @param ident Identifier for the setting. (See the gamemode config.)
	 * @return Value of the requested setting, 0 if unknown setting.
	 */
	float getFloatSetting(String ident);

	/**
	 * @param ident Identifier for the setting. (See the gamemode config.)
	 * @return Value of the requested setting, an itemstack containing an apple if unknown setting.
	 */
	ItemStack getStackSetting(String ident);

}
