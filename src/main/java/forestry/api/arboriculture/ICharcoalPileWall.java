/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

public interface ICharcoalPileWall {

	int getCharcoalAmount();

	boolean matches(BlockState state);

	NonNullList<ItemStack> getDisplayItems();

}
