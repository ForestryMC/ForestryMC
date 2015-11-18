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
package forestry.core.items;

import java.awt.Color;

public enum EnumElectronTube {
	COPPER(new Color(0xe3b78e)),
	TIN(new Color(0xE6F8FF)),
	BRONZE(new Color(0xddc276)),
	IRON(new Color(0xCCCCCC)),
	GOLD(new Color(0xffff8b)),
	DIAMOND(new Color(0x8CF5E3)),
	OBSIDIAN(new Color(0x866bc0)),
	BLAZE(new Color(0xd96600), new Color(0xFFF87E)),
	RUBBER(new Color(0x444444)),
	EMERALD(new Color(0x00CC41)),
	APATITE(new Color(0x579CD9)),
	LAPIS(new Color(0x1c57c6)),
	ENDER(new Color(0x33adad), new Color(0x255661));

	private final ItemOverlay.OverlayInfo overlayInfo;

	EnumElectronTube(Color color) {
		this(color, Color.WHITE);
	}

	EnumElectronTube(Color color, Color baseColor) {
		this.overlayInfo = new ItemOverlay.OverlayInfo("ex-" + ordinal(), baseColor, color);
	}

	public static ItemOverlay.OverlayInfo[] getAllOverlayInfo() {
		EnumElectronTube[] values = values();
		ItemOverlay.OverlayInfo[] overlayInfos = new ItemOverlay.OverlayInfo[values.length];
		for (EnumElectronTube electronTube : values) {
			overlayInfos[electronTube.ordinal()] = electronTube.overlayInfo;
		}
		return overlayInfos;
	}
}
