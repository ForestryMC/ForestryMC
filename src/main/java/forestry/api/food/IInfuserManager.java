/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.food;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface IInfuserManager {

	void addMixture(int meta, ItemStack ingredient, IBeverageEffect effect);

	void addMixture(int meta, NonNullList<ItemStack> ingredients, IBeverageEffect effect);

	ItemStack getSeasoned(ItemStack base, NonNullList<ItemStack> ingredients);

	boolean hasMixtures(NonNullList<ItemStack> ingredients);

	boolean isIngredient(ItemStack itemstack);

	NonNullList<ItemStack> getRequired(NonNullList<ItemStack> ingredients);

}
