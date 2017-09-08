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
package forestry.core.climate;

import forestry.api.climate.ClimateStateType;
import forestry.api.climate.IClimateInfo;
import forestry.api.climate.IClimateState;

@Deprecated
public class ClimateInfo implements IClimateInfo {
	private final float temperature;
	private final float humidity;

	public ClimateInfo(float temperature, float humidity) {
		this.temperature = ClimateStateType.DEFAULT.clamp(temperature);
		this.humidity = ClimateStateType.DEFAULT.clamp(humidity);
	}

	public ClimateInfo(IClimateState climateState) {
		this.temperature = climateState.copy(ClimateStateType.DEFAULT).getTemperature();
		this.humidity = climateState.copy(ClimateStateType.DEFAULT).getHumidity();
	}

	@Override
	public float getTemperature() {
		return temperature;
	}

	@Override
	public float getHumidity() {
		return humidity;
	}
}
