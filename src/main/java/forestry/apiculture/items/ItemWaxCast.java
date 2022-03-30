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
package forestry.apiculture.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import forestry.api.core.ItemGroups;
import forestry.core.items.ItemForestry;
import forestry.core.items.definitions.ICraftingPlan;

public class ItemWaxCast extends ItemForestry implements ICraftingPlan {

	public ItemWaxCast() {
		super((new Item.Properties())
				.tab(ItemGroups.tabApiculture)
				.durability(16)
			.setNoRepair());
	}

	@Override
	public ItemStack planUsed(ItemStack plan, ItemStack result) {
		plan.setDamageValue(plan.getDamageValue() + result.getCount());
		if (plan.getDamageValue() >= plan.getMaxDamage()) {
			return ItemStack.EMPTY;
		} else {
			return plan;
		}
	}

}
