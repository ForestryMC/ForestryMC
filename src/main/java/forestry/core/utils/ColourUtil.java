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

public class ColourUtil {

	public static int addRGBComponents(int colour, int r, int g, int b) {
		r = getRed(colour) + r;
		g = getGreen(colour) + g;
		b = getBlue(colour) + b;

		r = r <= 255 ? r : 255;
		g = g <= 255 ? g : 255;
		b = b <= 255 ? b : 255;

		return (r & 0x0ff) << 16 | (g & 0x0ff) << 8 | b & 0x0ff;
	}

	public static int multiplyRGBComponents(int colour, float factor) {
		int r = (int) (getRed(colour) * factor);
		int g = (int) (getGreen(colour) * factor);
		int b = (int) (getBlue(colour) * factor);

		r = r <= 255 ? r : 255;
		g = g <= 255 ? g : 255;
		b = b <= 255 ? b : 255;

		return (r & 0x0ff) << 16 | (g & 0x0ff) << 8 | b & 0x0ff;
	}

	public static int getRed(int colour) {
		return (colour & 0xff0000) >> 16;
	}

	public static int getGreen(int colour) {
		return (colour & 0xff00) >> 8;
	}

	public static int getBlue(int colour) {
		return colour & 0xff;
	}

	public static float getRedAsFloat(int colour) {
		return getRed(colour) / 255.0F;
	}

	public static float getGreenAsFloat(int colour) {
		return getGreen(colour) / 255.0F;
	}

	public static float getBlueAsFloat(int colour) {
		return getBlue(colour) / 255.0F;
	}
}
