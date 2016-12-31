/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

/**
 * Easily extendable default farmListener.
 * By itself, this farmListener does nothing.
 * FarmListeners should inherit from this class unless they need to listen for everything.
 */
public class DefaultFarmListener implements IFarmListener {
	@Override
	public boolean beforeCropHarvest(ICrop crop) {
		return false;
	}

	@Override
	public void afterCropHarvest(NonNullList<ItemStack> harvested, ICrop crop) {

	}

	@Override
	public void hasCollected(NonNullList<ItemStack> collected, IFarmLogic logic) {

	}

	@Override
	public void hasCultivated(IFarmLogic logic, BlockPos pos, FarmDirection direction, int extent) {

	}

	@Override
	public void hasScheduledHarvest(Collection<ICrop> harvested, IFarmLogic logic, BlockPos pos, FarmDirection direction, int extent) {

	}

	@Override
	public boolean cancelTask(IFarmLogic logic, FarmDirection direction) {
		return false;
	}

}
