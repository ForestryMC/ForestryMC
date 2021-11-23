/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import java.util.Collection;

import net.minecraft.item.ItemStack;

import net.minecraftforge.common.util.ForgeDirection;

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
	public void afterCropHarvest(Collection<ItemStack> harvested, ICrop crop) {

	}

	@Override
	public void hasCollected(Collection<ItemStack> collected, IFarmLogic logic) {

	}

	@Override
	public void hasCultivated(IFarmLogic logic, int x, int y, int z, FarmDirection direction, int extent) {

	}

	@Override
	public void hasScheduledHarvest(Collection<ICrop> harvested, IFarmLogic logic, int x, int y, int z, FarmDirection direction, int extent) {

	}

	@Override
	public boolean cancelTask(IFarmLogic logic, FarmDirection direction) {
		return false;
	}

}
