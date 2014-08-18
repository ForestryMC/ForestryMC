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

import java.util.IllegalFormatException;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class StringUtil {

	public static String localize(String key) {
		return StatCollector.translateToLocal(key).replace("\\n", "\n").replace("@", "%").replace("\\%", "@");
	}

	public static String localize(String key, Object... args) {
		String text = StringUtil.localize(key);

		try {
			return String.format(text, args);
		} catch (IllegalFormatException ex) {
			return "Format error: " + text;
		}
	}

	public static String cleanTags(String tag) {
		return tag.replaceAll("[Ff]orestry\\p{Punct}", "").replaceAll("\\.[Ff]or\\p{Punct}", ".").replaceFirst("^tile\\.", "").replaceFirst("^item\\.", "");
	}

	public static String cleanItemName(ItemStack stack) {
		return cleanTags(stack.getUnlocalizedName());
	}

	public static String cleanItemName(Item item) {
		return cleanTags(item.getUnlocalizedName());
	}

	public static String cleanBlockName(Block block) {
		return cleanTags(block.getUnlocalizedName());
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
