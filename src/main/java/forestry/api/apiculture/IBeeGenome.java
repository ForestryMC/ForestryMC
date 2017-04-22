/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IGenome;
import net.minecraft.util.math.Vec3i;

/**
 * Only the default implementation is supported.
 *
 * @author SirSengir
 */
public interface IBeeGenome extends IGenome {

	@Override
	IAlleleBeeSpecies getPrimary();

	@Override
	IAlleleBeeSpecies getSecondary();

	float getSpeed();

	int getLifespan();

	int getFertility();

	EnumTolerance getToleranceTemp();

	EnumTolerance getToleranceHumid();

	boolean getNeverSleeps();

	boolean getToleratesRain();

	boolean getCaveDwelling();

	IFlowerProvider getFlowerProvider();

	int getFlowering();

	Vec3i getTerritory();

	IAlleleBeeEffect getEffect();

}
