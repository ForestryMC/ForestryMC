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
package forestry.climatology.climate;

public enum ClimateSourceMode {
	POSITIVE(1), NEGATIVE(0), NONE(2);

	private int opposite;

	ClimateSourceMode(int opposite) {
		this.opposite = opposite;
	}

	public ClimateSourceMode getOpposite() {
		return values()[opposite];
	}

}
