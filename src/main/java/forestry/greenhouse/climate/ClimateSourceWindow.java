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
package forestry.greenhouse.climate;

import forestry.api.climate.IClimateState;
import forestry.api.core.ForestryAPI;
import forestry.core.climate.ClimateStates;
import forestry.greenhouse.tiles.TileGreenhouseWindow;
import forestry.greenhouse.tiles.TileGreenhouseWindow.WindowMode;

public class ClimateSourceWindow extends ClimateSource<TileGreenhouseWindow> {

	public ClimateSourceWindow(float change, float range) {
		super(change, range, ClimateSourceType.BOTH);
	}

	@Override
	protected void beforeWork() {
		IClimateState currentState = container.getState();
		IClimateState biomeState = ForestryAPI.climateManager.getBiomeState(owner.getWorld(), owner.getPos());
		if (biomeState.getTemperature() > currentState.getTemperature()) {
			setTemperatureMode(ClimateSourceMode.POSITIVE);
		} else if (biomeState.getTemperature() < currentState.getTemperature()) {
			setTemperatureMode(ClimateSourceMode.NEGATIVE);
		} else {
			setTemperatureMode(ClimateSourceMode.NONE);
		}
		if (biomeState.getHumidity() > currentState.getHumidity()) {
			setHumidityMode(ClimateSourceMode.POSITIVE);
		} else if (biomeState.getHumidity() < currentState.getHumidity()) {
			setHumidityMode(ClimateSourceMode.NEGATIVE);
		} else {
			setHumidityMode(ClimateSourceMode.NONE);
		}
	}

	@Override
	public boolean canWork(IClimateState currentState, ClimateSourceType oppositeType) {
		return owner.getMode() == WindowMode.OPEN;
	}

	@Override
	protected void removeResources(IClimateState currentState, ClimateSourceType oppositeType) {
	}

	@Override
	protected IClimateState getChange(ClimateSourceType type, IClimateState target, IClimateState currentState) {
		float temperature = 0.0F;
		float humidity = 0.0F;
		if (type.canChangeHumidity()) {
			if (humidityMode == ClimateSourceMode.NEGATIVE) {
				humidity -= change;
			} else {
				humidity += change;
			}
		}
		if (type.canChangeTemperature()) {
			if (temperatureMode == ClimateSourceMode.NEGATIVE) {
				temperature -= change;
			} else {
				temperature += change;
			}
		}
		return ClimateStates.extendedOf(temperature, humidity);
	}

}
