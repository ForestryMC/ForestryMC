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
import forestry.core.utils.Translator;

public class GenericRatings {

	public static String rateMetabolism(int metabolism) {
		if (metabolism >= 19) {
			return Translator.translateToLocal("forestry.allele.highest");
		} else if (metabolism >= 16) {
			return Translator.translateToLocal("forestry.allele.higher");
		} else if (metabolism >= 13) {
			return Translator.translateToLocal("forestry.allele.high");
		} else if (metabolism >= 10) {
			return Translator.translateToLocal("forestry.allele.average");
		} else if (metabolism >= 7) {
			return Translator.translateToLocal("forestry.allele.slow");
		} else if (metabolism >= 4) {
			return Translator.translateToLocal("forestry.allele.slower");
		} else {
			return Translator.translateToLocal("forestry.allele.slowest");
		}
	}

	public static String rateActivityTime(boolean neverSleeps, boolean naturalNocturnal) {
		String active = naturalNocturnal ? Translator.translateToLocal("for.gui.nocturnal") : Translator.translateToLocal("for.gui.diurnal");
		if (neverSleeps) {
			active = StringUtil.append(", ", active, naturalNocturnal ? Translator.translateToLocal("for.gui.diurnal") : Translator.translateToLocal("for.gui.nocturnal"));
		}

		return active;
	}
}
