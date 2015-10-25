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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import forestry.core.proxy.Proxies;

public class StringUtil {

	public static boolean canTranslate(String key) {
		return StatCollector.canTranslate("for." + key);
	}

	public static boolean canTranslateTile(String key) {
		return StatCollector.canTranslate("tile.for." + key);
	}

	public static String localize(String key) {
		return StatCollector.translateToLocal("for." + key).replace("\\n", "\n").replace("@", "%").replace("\\%", "@");
	}

	public static String localizeTile(String key) {
		return StatCollector.translateToLocal("tile.for." + key).replace("\\n", "\n").replace("@", "%").replace("\\%", "@");
	}

	public static String localizeAndFormat(String key, Object... args) {
		return localizeAndFormatRaw("for." + key, args);
	}

	/**
	 * Same as localizeAndFormat, only without the "for." prefix. Used for specific items.
	 */
	public static String localizeAndFormatRaw(String key, Object... args) {
		String text = StatCollector.translateToLocal(key).replace("\\n", "\n").replace("@", "%").replace("\\%", "@");

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

	public static String append(String delim, String source, String appendix) {
		if (source.length() <= 0) {
			return appendix;
		}

		if (appendix.length() <= 0) {
			return source;
		}

		return source + delim + appendix;
	}

	public static String readableBoolean(boolean flag, String trueStr, String falseStr) {
		if (flag) {
			return trueStr;
		} else {
			return falseStr;
		}
	}

	public static String floatAsPercent(float val) {
		return (int) (val * 100) + " %";
	}

	public static String line(int length) {
		StringBuilder line = new StringBuilder();
		for (int i = 0; i < length; i++) {
			line.append('-');
		}

		return line.toString();
	}

	public static int getLineHeight(int maxWidth, String... strings) {
		Minecraft minecraft = Proxies.common.getClientInstance();
		FontRenderer fontRenderer = minecraft.fontRenderer;

		int lineCount = 0;
		for (String string : strings) {
			lineCount += fontRenderer.listFormattedStringToWidth(string, maxWidth).size();
		}

		return lineCount * fontRenderer.FONT_HEIGHT;
	}
}
