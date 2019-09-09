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
package forestry.core.inventory;

import java.util.function.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.FurnaceTileEntity;

/**
 * This interface is used with several of the functions in IItemTransfer to
 * provide a convenient means of dealing with entire classes of items without
 * having to specify each item individually.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum StandardStackFilters implements Predicate<ItemStack> {

	ALL {
		@Override
		public boolean test(ItemStack stack) {
			return true;
		}
	},
	FUEL {
		@Override
		public boolean test(ItemStack stack) {
			return FurnaceTileEntity.getBurnTimes().get(stack.getItem()) > 0;
		}
	},
	//TODO - where is this used?
	FEED {
		@Override
		public boolean test(ItemStack stack) {
			//TODO - tag on seeds?
			return stack.getItem().getFood() != null || stack.getItem() == Items.WHEAT || stack.getItem() == Items.WHEAT_SEEDS;
		}
	}
}
