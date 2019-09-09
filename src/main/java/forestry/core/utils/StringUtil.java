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

import java.util.Locale;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
public class StringUtil {

	private static final Pattern camelCaseToUnderscores = Pattern.compile("(.)([A-Z])");

	public static String camelCaseToUnderscores(String uid) {
		return camelCaseToUnderscores.matcher(uid).replaceAll("$1_$2").toLowerCase(Locale.ENGLISH);
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

	@OnlyIn(Dist.CLIENT)
	public static int getLineHeight(int maxWidth, String... strings) {
		Minecraft minecraft = Minecraft.getInstance();
		FontRenderer fontRenderer = minecraft.fontRenderer;

		int lineCount = 0;
		for (String string : strings) {
			lineCount += fontRenderer.listFormattedStringToWidth(string, maxWidth).size();
		}

		return lineCount * fontRenderer.FONT_HEIGHT;
	}
}
