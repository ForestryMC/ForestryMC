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
package forestry.core.items;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

import forestry.core.ItemGroupForestry;

public class ItemForestryFood extends Item {

	private boolean isDrink = false;

	public ItemForestryFood(int heal) {
		this(heal, 0.6f);
	}

	public ItemForestryFood(Item.Properties properties) {
		super(properties);
	}

	public ItemForestryFood(int heal, float saturation) {
		this(heal, saturation, new Item.Properties());
	}

	public ItemForestryFood(int heal, float saturation, Item.Properties properties) {
		super(properties
				.tab(ItemGroupForestry.tabForestry)
			.food((new FoodProperties.Builder())
					.nutrition(heal)
					.saturationMod(saturation)
				.build()));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		if (isDrink) {
			return UseAnim.DRINK;
		} else {
			return UseAnim.EAT;
		}
	}

	public ItemForestryFood setIsDrink() {
		isDrink = true;
		return this;
	}

}
