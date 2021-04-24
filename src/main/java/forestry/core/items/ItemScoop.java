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

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.ToolType;

import forestry.api.core.IToolScoop;
import forestry.api.core.ItemGroups;

public class ItemScoop extends ItemForestry implements IToolScoop {
	public static ToolType SCOOP = ToolType.get("scoop");

	public ItemScoop() {
		super(new Item.Properties()
				.durability(10)
				.tab(ItemGroups.tabApiculture)
				.addToolType(SCOOP, 3));
	}

	@Override
	public float getDestroySpeed(ItemStack itemstack, BlockState state) {
		if (state.getBlock().isToolEffective(state, SCOOP)) {
			return 2.0F;
		}
		return 1.0F;
	}
}
