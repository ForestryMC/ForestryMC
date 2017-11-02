/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface ICharcoalPileWall {

	int getCharcoalAmount();
	
	boolean matches(IBlockState state);

	@Deprecated
	NonNullList<ItemStack> getDisplyItems();

	NonNullList<ItemStack> getDisplayItems();
	
}
