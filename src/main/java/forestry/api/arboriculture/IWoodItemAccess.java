/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.item.ItemStack;

public interface IWoodItemAccess {
	ItemStack getLog(EnumWoodType woodType, boolean fireproof);
	ItemStack getPlanks(EnumWoodType woodType, boolean fireproof);
	ItemStack getSlab(EnumWoodType woodType, boolean fireproof);
	ItemStack getFence(EnumWoodType woodType, boolean fireproof);
	ItemStack getStairs(EnumWoodType woodType, boolean fireproof);
}
