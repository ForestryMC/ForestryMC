/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.items;

import net.minecraft.item.ItemStack;

import forestry.core.interfaces.ICraftingPlan;
import forestry.core.items.ItemForestry;

public class ItemWaxCast extends ItemForestry implements ICraftingPlan {

	public ItemWaxCast() {
		super();
		setMaxStackSize(1);
		setMaxDamage(10);
		setNoRepair();
	}

	@Override
	public ItemStack planUsed(ItemStack plan, ItemStack result) {
		plan.setItemDamage(plan.getItemDamage() + result.stackSize);
		if (plan.getItemDamage() >= plan.getMaxDamage())
			return null;
		else
			return plan;
	}

}
