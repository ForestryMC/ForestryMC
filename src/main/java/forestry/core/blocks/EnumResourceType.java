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

import net.minecraft.util.IStringSerializable;

public enum EnumResourceType implements IStringSerializable {
	APATITE(0, true),
	COPPER(1, true),
	TIN(2, true),
	BRONZE(3, false);

	public static final EnumResourceType[] VALUES = values();

	private final int meta;
	private final boolean hasOre;

	EnumResourceType(int meta, boolean hasOre) {
		this.meta = meta;
		this.hasOre = hasOre;
	}

	public int getMeta() {
		return meta;
	}

	public boolean hasOre() {
		return hasOre;
	}

	@Override
	public String getName() {
		return name().toLowerCase(Locale.ENGLISH);
	}
}
