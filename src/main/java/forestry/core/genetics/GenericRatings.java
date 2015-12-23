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

import net.minecraft.util.StatCollector;

import forestry.core.utils.StringUtil;

public class GenericRatings {

	public static String rateMetabolism(int metabolism) {
		if (metabolism >= 19) {
			return StatCollector.translateToLocal("forestry.allele.highest");
		} else if (metabolism >= 16) {
			return StatCollector.translateToLocal("forestry.allele.higher");
		} else if (metabolism >= 13) {
			return StatCollector.translateToLocal("forestry.allele.high");
		} else if (metabolism >= 10) {
			return StatCollector.translateToLocal("forestry.allele.average");
		} else if (metabolism >= 7) {
			return StatCollector.translateToLocal("forestry.allele.slow");
		} else if (metabolism >= 4) {
			return StatCollector.translateToLocal("forestry.allele.slower");
		} else {
			return StatCollector.translateToLocal("forestry.allele.slowest");
		}
	}

	public static String rateActivityTime(boolean nocturnalTrait, boolean naturalNocturnal) {
		String active = naturalNocturnal ? StringUtil.localize("gui.nocturnal") : StringUtil.localize("gui.diurnal");
		if (nocturnalTrait) {
			active = StringUtil.append(", ", active, naturalNocturnal ? StringUtil.localize("gui.diurnal") : StringUtil.localize("gui.nocturnal"));
		}
		
		return active;
	}
}
