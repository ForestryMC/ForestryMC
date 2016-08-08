/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.fuels;

import net.minecraft.item.ItemStack;

public class FermenterFuel {
	private final ItemStack item;
	private final int fermentPerCycle;
	private final int burnDuration;

	public FermenterFuel(ItemStack item, int fermentPerCycle, int burnDuration) {
		this.item = item;
		this.fermentPerCycle = fermentPerCycle;
		this.burnDuration = burnDuration;
	}

	/**
	 * Item that is a valid fuel for the fermenter (i.e. fertilizer).
	 */
	public ItemStack getItem() {
		return item;
	}

	/**
	 * How much is fermeted per work cycle, i.e. how much biomass is produced per cycle.
	 */
	public int getFermentPerCycle() {
		return fermentPerCycle;
	}

	/**
	 * Amount of work cycles a single item of this fuel lasts before expiring.
	 */
	public int getBurnDuration() {
		return burnDuration;
	}
}
