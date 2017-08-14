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
package forestry.greenhouse.blocks;

import java.util.Locale;

import net.minecraft.util.IStringSerializable;

public enum BlockClimatiserType implements IStringSerializable {
	HYGRO,
	//temperature
	HEATER, FAN,
	//humidity
	HUMIDIFIER, DEHUMIDIFIER;

	public static final BlockClimatiserType[] VALUES = values();

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getName() {
		return name().toLowerCase(Locale.ENGLISH);
	}

}
