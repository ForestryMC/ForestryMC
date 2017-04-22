/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.food;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

public interface IIngredientManager {

	@Nullable
	String getDescription(ItemStack itemstack);

	void addIngredient(ItemStack ingredient, String description);

}
