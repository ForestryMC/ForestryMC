/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.utils;

public class StringUtil {

	public static String localize(String key) {
		return Localization.instance.get(key);
	}

	public static String localizeAndFormat(String key, Object... args) {
		return String.format(Localization.instance.get(key), args);
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
