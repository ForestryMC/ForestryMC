/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.farming;

import java.util.Collection;
import java.util.HashMap;

public class Farmables {
	/**
	 * Can be used to add IFarmables to some of the vanilla farm logics.
	 * 
	 * Identifiers: farmArboreal farmWheat farmGourd farmInfernal farmPoales farmSucculentes farmVegetables farmShroom
	 */
	public static HashMap<String, Collection<IFarmable>> farmables = new HashMap<String, Collection<IFarmable>>();

	public static IFarmInterface farmInterface;
}
