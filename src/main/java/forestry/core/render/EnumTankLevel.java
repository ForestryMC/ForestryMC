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
package forestry.core.render;

import net.minecraftforge.fluids.IFluidTank;

public enum EnumTankLevel {

	EMPTY(0),
	LOW(25),
	MEDIUM(50),
	HIGH(75),
	MAXIMUM(100);

	private final int level;

	EnumTankLevel(int level) {
		this.level = level;
	}

	public int getLevelScaled(int scale) {
		return level * scale / 100;
	}

	public static EnumTankLevel rateTankLevel(IFluidTank tank) {
		return rateTankLevel(100 * tank.getFluidAmount() / tank.getCapacity());
	}

	public static EnumTankLevel rateTankLevel(int scaled) {

		if (scaled < 5) {
			return EMPTY;
		} else if (scaled < 30) {
			return LOW;
		} else if (scaled < 60) {
			return MEDIUM;
		} else if (scaled < 90) {
			return HIGH;
		} else {
			return MAXIMUM;
		}
	}
}
