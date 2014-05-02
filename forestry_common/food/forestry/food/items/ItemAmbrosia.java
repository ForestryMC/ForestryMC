/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.food.items;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

import forestry.core.config.Defaults;
import forestry.core.items.ItemForestryFood;

public class ItemAmbrosia extends ItemForestryFood {

	public ItemAmbrosia() {
		super(Defaults.FOOD_AMBROSIA_HEAL, 0.6f);
		setAlwaysEdible();
		setPotionEffect(Potion.regeneration.id, 40, 0, 1.0F);
	}

	@Override
	public boolean hasEffect(ItemStack itemstack) {
		return true;
	}

}
