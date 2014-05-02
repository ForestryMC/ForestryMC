/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.genetics;

public enum EnumMutateChance {
	NONE(0xffffff), LOWEST(0xffba77), LOW(0xfffd77), NORMAL(0xfffd77), HIGH(0xfffd77), HIGHER(0xbeff77), HIGHEST(0x7bff77);

	public final int colour;
	
	private EnumMutateChance(int colour) {
		this.colour = colour;
	}
	
	public static EnumMutateChance rateChance(float percent) {

		if (percent >= 20)
			return HIGHEST;
		else if (percent >= 15)
			return HIGHER;
		else if (percent >= 12)
			return HIGH;
		else if (percent >= 10)
			return NORMAL;
		else if (percent >= 5)
			return LOW;
		else
			return LOWEST;

	}
}
