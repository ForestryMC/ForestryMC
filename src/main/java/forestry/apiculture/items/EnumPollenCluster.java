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
package forestry.apiculture.items;

import java.awt.Color;
import java.util.Locale;

import forestry.core.items.ItemOverlay;

public enum EnumPollenCluster implements ItemOverlay.IOverlayInfo {
	NORMAL(new Color(0xa28a25), new Color(0xa28a25)),
	CRYSTALLINE(new Color(0xffffff), new Color(0xc5feff));

	public static final EnumPollenCluster[] VALUES = values();

	private final String name;
	private final int primaryColor;
	private final int secondaryColor;

	EnumPollenCluster(Color primary, Color secondary) {
		this.name = toString().toLowerCase(Locale.ENGLISH);
		this.primaryColor = primary.getRGB();
		this.secondaryColor = secondary.getRGB();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getPrimaryColor() {
		return primaryColor;
	}

	@Override
	public int getSecondaryColor() {
		return secondaryColor;
	}

	@Override
	public boolean isSecret() {
		return false;
	}
}
