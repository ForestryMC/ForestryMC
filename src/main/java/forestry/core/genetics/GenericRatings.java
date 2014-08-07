/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
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
