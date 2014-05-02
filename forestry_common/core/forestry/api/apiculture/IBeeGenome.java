/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.apiculture;

import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IGenome;

/**
 * Only the default implementation is supported.
 * 
 * @author SirSengir
 * 
 */
public interface IBeeGenome extends IGenome {

	IAlleleBeeSpecies getPrimary();
	
	IAlleleBeeSpecies getSecondary();

	float getSpeed();

	int getLifespan();

	int getFertility();

	EnumTolerance getToleranceTemp();

	EnumTolerance getToleranceHumid();

	boolean getNocturnal();

	boolean getTolerantFlyer();

	boolean getCaveDwelling();

	IFlowerProvider getFlowerProvider();

	int getFlowering();

	int[] getTerritory();

	IAlleleBeeEffect getEffect();

}
