/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.fuels;

import net.minecraft.item.ItemStack;

public class FermenterFuel {
	/**
	 * Item that is a valid fuel for the fermenter (i.e. fertilizer).
	 */
	public final ItemStack item;
	/**
	 * How much is fermeted per work cycle, i.e. how much biomass is produced per cycle.
	 */
	public final int fermentPerCycle;
	/**
	 * Amount of work cycles a single item of this fuel lasts before expiring.
	 */
	public final int burnDuration;

	public FermenterFuel(ItemStack item, int fermentPerCycle, int burnDuration) {
		this.item = item;
		this.fermentPerCycle = fermentPerCycle;
		this.burnDuration = burnDuration;
	}
}
