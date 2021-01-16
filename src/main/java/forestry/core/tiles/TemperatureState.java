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
package forestry.core.tiles;

public enum TemperatureState {

	UNKNOWN,
	COOL,
	WARMED_UP,
	OPERATING_TEMPERATURE,
	RUNNING_HOT,
	OVERHEATING,
	MELTING;

	public static TemperatureState getState(double heat, double maxHeat) {
		final double scaledHeat = heat / maxHeat;

		if (scaledHeat < 0.20) {
			return TemperatureState.COOL;
		} else if (scaledHeat < 0.45) {
			return TemperatureState.WARMED_UP;
		} else if (scaledHeat < 0.65) {
			return TemperatureState.OPERATING_TEMPERATURE;
		} else if (scaledHeat < 0.85) {
			return TemperatureState.RUNNING_HOT;
		} else if (scaledHeat < 1.0) {
			return TemperatureState.OVERHEATING;
		} else {
			return TemperatureState.MELTING;
		}
	}

}
