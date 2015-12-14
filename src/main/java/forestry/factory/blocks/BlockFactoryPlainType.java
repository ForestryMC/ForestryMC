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
package forestry.factory.blocks;

import java.util.Locale;

import org.apache.commons.lang3.text.WordUtils;

import forestry.core.blocks.IMachineProperties;
import forestry.core.tiles.TileForestry;
import forestry.factory.tiles.TileFabricator;
import forestry.factory.tiles.TileRaintank;
import forestry.factory.tiles.TileWorktable;

public enum BlockFactoryPlainType implements IMachineProperties {
	FABRICATOR(TileFabricator.class),
	RAINTANK(TileRaintank.class),
	WORKTABLE(TileWorktable.class);

	public static final BlockFactoryPlainType[] VALUES = values();

	private final String teIdent;
	private final Class<? extends TileForestry> teClass;

	BlockFactoryPlainType(Class<? extends TileForestry> teClass) {
		String name = toString().toLowerCase(Locale.ENGLISH);
		this.teIdent = "forestry." + WordUtils.capitalize(name);
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
