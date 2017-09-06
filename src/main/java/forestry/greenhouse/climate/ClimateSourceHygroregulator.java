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

import net.minecraftforge.fluids.FluidStack;

import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateState;
import forestry.api.core.IErrorLogic;
import forestry.core.climate.ClimateSourceMode;
import forestry.core.climate.ClimateSourceType;
import forestry.core.climate.ClimateStates;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FilteredTank;
import forestry.core.recipes.HygroregulatorRecipe;
import forestry.greenhouse.tiles.TileHygroregulator;

public class ClimateSourceHygroregulator extends ClimateSourceCircuitable<TileHygroregulator> {

	private HygroregulatorRecipe currentRecipe;

	public ClimateSourceHygroregulator(float range) {
		super(0.0F, range, ClimateSourceType.BOTH);
	}

	@Override
	protected void beforeWork() {
		currentRecipe = null;
		createRecipe();
		if (currentRecipe != null) {
			float tempChange = currentRecipe.tempChange;
			float humidChange = currentRecipe.humidChange;
			if (tempChange > 0) {
				setTemperatureMode(ClimateSourceMode.POSITIVE);
			} else if (tempChange < 0) {
				setTemperatureMode(ClimateSourceMode.NEGATIVE);
			} else {
				setTemperatureMode(ClimateSourceMode.NONE);
			}
			if (humidChange > 0) {
				setHumidityMode(ClimateSourceMode.POSITIVE);
			} else if (humidChange < 0) {
				setHumidityMode(ClimateSourceMode.NEGATIVE);
			} else {
				setHumidityMode(ClimateSourceMode.NONE);
			}
		} else {
			setTemperatureMode(ClimateSourceMode.NONE);
			setHumidityMode(ClimateSourceMode.NONE);
		}
	}

	@Override
	public boolean canWork(IClimateState state, IClimateState target) {
		createRecipe();
		FilteredTank liquidTank = owner.getLiquidTank();
		IErrorLogic errorLogic = owner.getErrorLogic();
		if (currentRecipe != null && liquidTank.drainInternal(currentRecipe.liquid.amount, false) != null) {
			errorLogic.setCondition(false, EnumErrorCode.NO_RESOURCE_LIQUID);
			return true;
		}
		errorLogic.setCondition(true, EnumErrorCode.NO_RESOURCE_LIQUID);
		return false;
	}

	@Override
	protected void removeResources(IClimateState state, IClimateState target) {
		FilteredTank liquidTank = owner.getLiquidTank();
		liquidTank.drainInternal(currentRecipe.liquid.amount, true);
	}

	@Override
	protected IClimateState getChange(ClimateSourceType type, IClimateState state, IClimateState target) {
		float temperature = 0.0F;
		float humidity = 0.0F;
		if (type.canChangeHumidity()) {
			humidity += currentRecipe.humidChange * getChangeMultiplier(ClimateType.HUMIDITY);
		}
		if (type.canChangeTemperature()) {
			temperature += currentRecipe.tempChange * getChangeMultiplier(ClimateType.TEMPERATURE);
		}
		return ClimateStates.changeOf(temperature, humidity);
	}

	private void createRecipe() {
		if (currentRecipe == null) {
			FilteredTank liquidTank = owner.getLiquidTank();
			FluidStack fluid = liquidTank.getFluid();
			if (fluid != null && fluid.amount > 0) {
				currentRecipe = owner.getRecipe(fluid);
			} else {
				currentRecipe = null;
			}
		}
	}

}
