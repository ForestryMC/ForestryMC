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

import forestry.core.ItemGroupForestry;
import forestry.core.config.Constants;
import forestry.core.items.ItemForestryFood;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class ItemAmbrosia extends ItemForestryFood {

    public ItemAmbrosia() {
        super((new Item.Properties())
                .group(ItemGroupForestry.tabForestry)
                .food(new Food.Builder()
                        .setAlwaysEdible()
                        .hunger(Constants.FOOD_AMBROSIA_HEAL)
                        .saturation(0.6f)
                        .effect(new EffectInstance(Effects.REGENERATION, 40, 0), 1.0F)
                        .build()));
    }

    @Override
    public boolean hasEffect(ItemStack itemstack) {
        return true;
    }

}
