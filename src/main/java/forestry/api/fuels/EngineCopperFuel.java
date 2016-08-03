/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.fuels;

import net.minecraft.item.ItemStack;

public class EngineCopperFuel {

	private final ItemStack fuel;
	private final int powerPerCycle;
	private final int burnDuration;

	public EngineCopperFuel(ItemStack fuel, int powerPerCycle, int burnDuration) {
		this.fuel = fuel;
		this.powerPerCycle = powerPerCycle;
		this.burnDuration = burnDuration;
	}

	/**
	 * Item that is valid fuel for a peat-fired engine.
	 */
	public ItemStack getFuel() {
		return fuel;
	}

	/**
	 * Power produced by this fuel per work cycle.
	 */
	public int getPowerPerCycle() {
		return powerPerCycle;
	}

	/**
	 * Amount of work cycles this item lasts before being consumed.
	 */
	public int getBurnDuration() {
		return burnDuration;
	}
}
