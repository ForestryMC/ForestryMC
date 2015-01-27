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
package forestry.core.inventory.filters;

import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

/**
 * This interface is used with several of the functions in IItemTransfer to
 * provide a convenient means of dealing with entire classes of items without
 * having to specify each item individually.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum StackFilter implements IStackFilter {

	ALL {
		@Override
		public boolean matches(ItemStack stack) {
			return true;
		}
	},
	FUEL {
		@Override
		public boolean matches(ItemStack stack) {
			return TileEntityFurnace.getItemBurnTime(stack) > 0;
		}
	},
	FEED {
		@Override
		public boolean matches(ItemStack stack) {
			return stack.getItem() instanceof ItemFood || stack.getItem() == Items.wheat || stack.getItem() instanceof ItemSeeds;
		}
	};

	@Override
	public abstract boolean matches(ItemStack stack);
}
