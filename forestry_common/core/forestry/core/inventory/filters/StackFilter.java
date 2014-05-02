/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
