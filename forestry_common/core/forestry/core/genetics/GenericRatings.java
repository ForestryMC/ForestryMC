/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.genetics;

import forestry.core.utils.StringUtil;

public class GenericRatings {

	public static String rateMetabolism(int metabolism) {
		if (metabolism >= 19)
			return StringUtil.localize("gui.highest");
		else if (metabolism >= 16)
			return StringUtil.localize("gui.higher");
		else if (metabolism >= 13)
			return StringUtil.localize("gui.high");
		else if (metabolism >= 10)
			return StringUtil.localize("gui.average");
		else if (metabolism >= 7)
			return StringUtil.localize("gui.slow");
		else if (metabolism >= 4)
			return StringUtil.localize("gui.slower");
		else
			return StringUtil.localize("gui.slowest");
	}

	public static String rateActivityTime(boolean nocturnalTrait, boolean naturalNocturnal) {
		String active = naturalNocturnal ? StringUtil.localize("gui.nocturnal") : StringUtil.localize("gui.diurnal");
		if(nocturnalTrait)
			active = StringUtil.append(", ", active, naturalNocturnal ? StringUtil.localize("gui.diurnal") : StringUtil.localize("gui.nocturnal"));
		
		return active;
	}
}
