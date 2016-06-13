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
package forestry.api.core;

public interface IClimateControlled {
	/**
	 * Change the temperature, but not lower than boundaryDown or higher than boundaryUp.
	 */
	void addTemperatureChange(float change, float boundaryDown, float boundaryUp);

	/**
	 * Change the humidity, but not lower than boundaryDown or higher than boundaryUp.
	 */
	void addHumidityChange(float change, float boundaryDown, float boundaryUp);
}
