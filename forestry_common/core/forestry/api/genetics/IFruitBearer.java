/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.genetics;

import java.util.Collection;

import net.minecraft.item.ItemStack;

/**
 * Can be implemented by tile entities which can bear fruit.
 * 
 * @author SirSengir
 */
public interface IFruitBearer {

	/**
	 * @return true if the actual tile can bear fruits.
	 */
	boolean hasFruit();

	/**
	 * @return Family of the potential fruits on this tile.
	 */
	IFruitFamily getFruitFamily();

	/**
	 * Picks the fruits of this tile, resetting it to unripe fruits.
	 * 
	 * @param tool
	 *            Tool used in picking the fruits. May be null.
	 * @return Picked fruits.
	 */
	Collection<ItemStack> pickFruit(ItemStack tool);

	/**
	 * @return float indicating the ripeness of the fruit with >= 1.0f indicating full ripeness.
	 */
	float getRipeness();

	/**
	 * Increases the ripeness of the fruit.
	 * 
	 * @param add
	 *            Float to add to the ripeness. Will truncate to valid values.
	 */
	void addRipeness(float add);
}
