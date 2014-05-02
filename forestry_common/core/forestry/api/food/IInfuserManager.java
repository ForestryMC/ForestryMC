/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.food;

import net.minecraft.item.ItemStack;

public interface IInfuserManager {

	void addMixture(int meta, ItemStack ingredient, IBeverageEffect effect);

	void addMixture(int meta, ItemStack[] ingredients, IBeverageEffect effect);

	ItemStack getSeasoned(ItemStack base, ItemStack[] ingredients);

	boolean hasMixtures(ItemStack[] ingredients);

	ItemStack[] getRequired(ItemStack[] ingredients);

}
