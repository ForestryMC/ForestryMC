/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.lepidopterology;

import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IGenome;

public interface IButterflyGenome extends IGenome {
	
	IAlleleButterflySpecies getPrimary();

	IAlleleButterflySpecies getSecondary();

	float getSize();

	int getLifespan();

	int getMetabolism();
	
	int getFertility();

	float getSpeed();

	EnumTolerance getToleranceTemp();

	EnumTolerance getToleranceHumid();

	boolean getNocturnal();

	boolean getTolerantFlyer();

	boolean getFireResist();

	IFlowerProvider getFlowerProvider();

	IAlleleButterflyEffect getEffect();

}
