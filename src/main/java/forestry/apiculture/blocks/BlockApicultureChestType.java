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

import forestry.apiculture.tiles.TileApiaristChest;
import forestry.core.blocks.IMachineProperties;
import forestry.core.tiles.TileForestry;

public enum BlockApicultureChestType implements IMachineProperties {
	APIARIST_CHEST(TileApiaristChest.class, "ApiaristChest");

	public static final BlockApicultureChestType[] VALUES = values();

	private final String teIdent;
	private final Class<? extends TileForestry> teClass;

	BlockApicultureChestType(Class<? extends TileForestry> teClass, String name) {
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
	
	@Override
	public String getName() {
		return name().toLowerCase(Locale.ENGLISH);
	}
}
