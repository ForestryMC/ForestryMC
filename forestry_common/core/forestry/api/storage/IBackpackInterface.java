/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.storage;

import net.minecraft.item.Item;

public interface IBackpackInterface {

	/**
	 * Adds a backpack with the given definition and type, returning the item.
	 * 
	 * @param definition
	 *            Definition of backpack behaviour.
	 * @param type
	 *            Type of backpack. (T1 or T2 (= Woven)
	 * @return Created backpack item.
	 */
	Item addBackpack(IBackpackDefinition definition, EnumBackpackType type);
}
