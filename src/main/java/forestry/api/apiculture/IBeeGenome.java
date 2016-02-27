/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import javax.annotation.Nonnull;

import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IGenome;

/**
 * Only the default implementation is supported.
 *
 * @author SirSengir
 *
 */
public interface IBeeGenome extends IGenome<BeeChromosome> {

	@Nonnull
	@Override
	IAlleleBeeSpecies getPrimary();
	
	@Nonnull
	@Override
	IAlleleBeeSpecies getSecondary();

	float getSpeed();

	int getLifespan();

	int getFertility();

	@Nonnull
	EnumTolerance getToleranceTemp();

	@Nonnull
	EnumTolerance getToleranceHumid();

	boolean getNocturnal();

	boolean getTolerantFlyer();

	boolean getCaveDwelling();

	@Nonnull
	IFlowerProvider getFlowerProvider();

	int getFlowering();

	int[] getTerritory();

	@Nonnull
	IAlleleBeeEffect getEffect();

	@Nonnull
	@Override
	IBeeRoot getSpeciesRoot();
}
