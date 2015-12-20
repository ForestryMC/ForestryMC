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
package forestry.core.blocks;

import java.util.Locale;

import forestry.core.tiles.TileAnalyzer;
import forestry.core.tiles.TileEscritoire;
import forestry.core.tiles.TileForestry;
import net.minecraft.util.IStringSerializable;

public enum BlockCoreType implements IMachineProperties, IStringSerializable {
	ANALYZER(TileAnalyzer.class, "Analyzer"),
	ESCRITOIRE(TileEscritoire.class, "Escritoire");

	public static final BlockCoreType[] VALUES = values();

	private final String teIdent;
	private final Class<? extends TileForestry> teClass;

	BlockCoreType(Class<? extends TileForestry> teClass, String teName) {
		this.teIdent = "forestry." + teName;
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
		return getName().toLowerCase(Locale.ENGLISH);
	}
}
