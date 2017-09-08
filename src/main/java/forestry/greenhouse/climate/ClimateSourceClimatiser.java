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

import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateState;
import forestry.api.core.IErrorLogic;
import forestry.api.greenhouse.IClimateHousing;
import forestry.core.climate.ClimateStates;
import forestry.core.config.Config;
import forestry.core.errors.EnumErrorCode;
import forestry.energy.EnergyManager;
import forestry.greenhouse.api.climate.IClimateContainer;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import forestry.greenhouse.tiles.TileClimatiser;

public class ClimateSourceClimatiser<O extends TileClimatiser> extends ClimateSourceCircuitable<O> {

	protected static final int ENERGY_PER_OPERATION = 75;

	public ClimateSourceClimatiser(ClimateSourceType type, float change, float range) {
		super(change, range, type);
		if (type.canChangeTemperature()) {
			setTemperatureMode(change > 0 ? ClimateSourceMode.POSITIVE : ClimateSourceMode.NEGATIVE);
		} else {
			setHumidityMode(change > 0 ? ClimateSourceMode.POSITIVE : ClimateSourceMode.NEGATIVE);
		}
	}

	@Override
	public void onRemoved(IClimateContainer container) {
		super.onRemoved(container);
		owner.setActive(false);
	}

	@Override
	protected void isNotValid() {
		owner.setActive(false);
	}

	@Override
	public boolean canWork(IClimateState currentState, ClimateSourceType oppositeType) {
		IClimateHousing region = container.getParent();
		if (region instanceof IGreenhouseControllerInternal) {
			IGreenhouseControllerInternal controller = (IGreenhouseControllerInternal) region;
			IErrorLogic errorLogic = owner.getErrorLogic();
			EnergyManager energyManager = controller.getEnergyManager();

			if (energyManager.extractEnergy((int) (ENERGY_PER_OPERATION * getEnergyModifier(currentState, oppositeType)), true) > 0) {
				owner.setActive(true);
				errorLogic.setCondition(false, EnumErrorCode.NO_POWER);
				return true;
			} else {
				owner.setActive(false);
				errorLogic.setCondition(true, EnumErrorCode.NO_POWER);
				return false;
			}
		}
		if (owner.isActive()) {
			owner.setActive(false);
		}
		return false;
	}

	@Override
	protected void removeResources(IClimateState currentState, ClimateSourceType oppositeType) {
		IClimateHousing region = container.getParent();
		if (region instanceof IGreenhouseControllerInternal) {
			IGreenhouseControllerInternal controller = (IGreenhouseControllerInternal) region;

			EnergyManager energyManager = controller.getEnergyManager();

			energyManager.extractEnergy((int) (ENERGY_PER_OPERATION * getEnergyModifier(currentState, oppositeType)), false);
		}
	}

	protected float getEnergyModifier(IClimateState currentState, ClimateSourceType oppositeType) {
		float change = oppositeType.canChangeTemperature() ? currentState.getTemperature() : oppositeType.canChangeHumidity() ? currentState.getHumidity() : 0.0F;
		return 1.0F + change * Config.climateSourceEnergyModifier;
	}

	@Override
	protected IClimateState getChange(ClimateSourceType type, IClimateState target, IClimateState currentState) {
		float temperature = 0.0F;
		float humidity = 0.0F;
		if (type.canChangeHumidity()) {
			humidity += getChange(ClimateType.HUMIDITY);
		}
		if (type.canChangeTemperature()) {
			temperature += getChange(ClimateType.TEMPERATURE);
		}
		return ClimateStates.extendedOf(temperature, humidity);
	}

}
