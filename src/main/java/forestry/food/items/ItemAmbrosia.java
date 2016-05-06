/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.food.items;

import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

import forestry.core.config.Constants;
import forestry.core.items.ItemForestryFood;

public class ItemAmbrosia extends ItemForestryFood {

	public ItemAmbrosia() {
		super(Constants.FOOD_AMBROSIA_HEAL, 0.6f);
		setAlwaysEdible();
		PotionEffect potionEffect = new PotionEffect(MobEffects.REGENERATION, 40, 0);
		setPotionEffect(potionEffect, 1.0F);
	}

	@Override
	public boolean hasEffect(ItemStack itemstack) {
		return true;
	}

}
