/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.fuels;

import net.minecraftforge.fluids.Fluid;

public class EngineBronzeFuel {
	/**
	 * Item that is valid fuel for a biogas engine.
	 */
	public final Fluid liquid;
	/**
	 * Power produced by this fuel per work cycle of the engine.
	 */
	public final int powerPerCycle;
	/**
	 * How many work cycles a single "stack" of this type lasts.
	 */
	public final int burnDuration;
	/**
	 * By how much the normal heat dissipation rate of 1 is multiplied when using this fuel type.
	 */
	public final int dissipationMultiplier;

	public EngineBronzeFuel(Fluid liquid, int powerPerCycle, int burnDuration, int dissipationMultiplier) {
		this.liquid = liquid;
		this.powerPerCycle = powerPerCycle;
		this.burnDuration = burnDuration;
		this.dissipationMultiplier = dissipationMultiplier;
	}
}
