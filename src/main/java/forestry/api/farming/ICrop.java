/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

public interface ICrop {

	/**
	 * Harvests this crop. Performs the necessary manipulations to set the crop into a "harvested" state.
	 *
	 * @return Products harvested. Null if this crop cannot be harvested
	 */
	@Nullable
	NonNullList<ItemStack> harvest();

	BlockPos getPosition();
}
