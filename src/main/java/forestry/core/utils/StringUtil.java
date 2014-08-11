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
package forestry.core.utils;

import net.minecraft.util.StatCollector;

public class StringUtil {

	public static String localize(String key) {
		return StatCollector.translateToLocal(key);
	}

	public static String localizeAndFormat(String key, Object... args) {
		return StatCollector.translateToLocalFormatted(key, args);
	}

	public static String capitalize(String s) {
		if (s.length() == 0)
			return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	public static String append(String delim, String source, String appendix) {
		if (source.length() <= 0)
			return appendix;

		if (appendix.length() <= 0)
			return source;

		return source + delim + appendix;
	}

	public static String readableBoolean(boolean flag, String trueStr, String falseStr) {
		if (flag)
			return trueStr;
		else
			return falseStr;
	}

	public static String floatAsPercent(float val) {
		return (int) (val * 100) + " %";
	}
}
