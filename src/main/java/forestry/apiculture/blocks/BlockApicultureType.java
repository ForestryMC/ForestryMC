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
package forestry.apiculture.blocks;

import java.util.Locale;

import org.apache.commons.lang3.text.WordUtils;

import forestry.apiculture.tiles.TileApiaristChest;
import forestry.apiculture.tiles.TileApiary;
import forestry.apiculture.tiles.TileBeehouse;
import forestry.core.blocks.IMachineProperties;
import forestry.core.tiles.TileForestry;

public enum BlockApicultureType implements IMachineProperties {
	APIARY(TileApiary.class),
	APIARIST_CHEST_LEGACY(TileApiaristChest.class, "ApiaristChest"),
	BEEHOUSE(TileBeehouse.class);

	public static final BlockApicultureType[] VALUES = values();

	private final String teIdent;
	private final Class<? extends TileForestry> teClass;

	BlockApicultureType(Class<? extends TileForestry> teClass) {
		String name = toString().toLowerCase(Locale.ENGLISH);
		this.teIdent = "forestry." + WordUtils.capitalize(name);
		this.teClass = teClass;
	}

	BlockApicultureType(Class<? extends TileForestry> teClass, String name) {
		this.teIdent = "forestry." + name;
		this.teClass = teClass;
	}

	@Override
	public int getMeta() {
		return ordinal();
	}

	@Override
	public String getTeIdent() {
		return teIdent;
	}

	@Override
	public Class<? extends TileForestry> getTeClass() {
		return teClass;
	}
}
