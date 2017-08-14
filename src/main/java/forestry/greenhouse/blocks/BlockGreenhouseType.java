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

public enum BlockGreenhouseType implements IStringSerializable {
	PLAIN,
	BORDER,
	BORDER_CENTER(true),
	//Item Name = Energy Distributor, to have a deference between the farm gearbox and this one
	GEARBOX,
	CONTROL,
	SCREEN(true);

	public static final BlockGreenhouseType[] VALUES = values();

	public final boolean twoLayers;

	BlockGreenhouseType(boolean twoLayers) {
		this.twoLayers = twoLayers;
	}

	BlockGreenhouseType() {
		this(false);
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getName() {
		return name().toLowerCase(Locale.ENGLISH);
	}
}
