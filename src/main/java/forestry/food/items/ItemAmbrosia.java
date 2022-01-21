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

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import forestry.core.ItemGroupForestry;
import forestry.core.config.Constants;
import forestry.core.items.ItemForestryFood;

public class ItemAmbrosia extends ItemForestryFood {

	public ItemAmbrosia() {
		super((new Item.Properties())
				.tab(ItemGroupForestry.tabForestry)
			.food(new FoodProperties.Builder()
					.alwaysEat()
					.nutrition(Constants.FOOD_AMBROSIA_HEAL)
					.saturationMod(0.6f)
				.effect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0), 1.0F)
				.build()));
	}

	@Override
	public boolean isFoil(ItemStack itemstack) {
		return true;
	}

}
