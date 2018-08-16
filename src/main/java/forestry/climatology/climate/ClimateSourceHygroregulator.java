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

/*public class ClimateSourceHygroregulator extends ClimateSource<TileHygroregulator> {

	@Nullable
	private HygroregulatorRecipe currentRecipe;

	public ClimateSourceHygroregulator(TileHygroregulator proxy, float boundModifier) {
		super(proxy, 0.0F, boundModifier, ClimateSourceType.BOTH);
	}

	@Override
	protected void beforeWork() {
		currentRecipe = null;
		createRecipe();
		if (currentRecipe != null) {
			float tempChange = currentRecipe.getTempChange();
			float humidChange = currentRecipe.getHumidChange();
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
	public boolean canWork(IClimateState currentState, float resourceModifier) {
		createRecipe();
		FilteredTank liquidTank = proxy.getLiquidTank();
		IErrorLogic errorLogic = proxy.getErrorLogic();
		if (currentRecipe != null && liquidTank.drainInternal(Math.round(currentRecipe.getResource().amount * getEnergyModifier(currentState) * resourceModifier), false) != null) {
			errorLogic.setCondition(false, EnumErrorCode.NO_RESOURCE_LIQUID);
			proxy.setActive(true);
			return true;
		}
		errorLogic.setCondition(true, EnumErrorCode.NO_RESOURCE_LIQUID);
		proxy.setActive(false);
		return false;
	}

	@Override
	protected void removeResources(IClimateState currentState, float resourceModifier) {
		if (currentRecipe == null) {
			return;
		}
		FilteredTank liquidTank = proxy.getLiquidTank();
		liquidTank.drainInternal(Math.round(currentRecipe.getResource().amount * getEnergyModifier(currentState) * resourceModifier), true);
	}

	@Override
	protected float getChange(ClimateType type) {
		if(currentRecipe == null){
			return 0.0F;
		}
		if(type == ClimateType.HUMIDITY){
			return currentRecipe.getHumidChange();
		}
		return currentRecipe.getTempChange();
	}

	private void createRecipe() {
		if (currentRecipe == null) {
			FilteredTank liquidTank = proxy.getLiquidTank();
			FluidStack fluid = liquidTank.getFluid();
			if (fluid != null && fluid.amount > 0) {
				currentRecipe = proxy.getRecipe(fluid);
			} else {
				currentRecipe = null;
			}
		}
	}

}*/
