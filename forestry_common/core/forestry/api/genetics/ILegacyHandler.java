/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.genetics;

/**
 * AlleleManager.alleleRegistry can be cast to this type.
 */
public interface ILegacyHandler {
	void registerLegacyMapping(int id, String uid);

	IAllele getFromLegacyMap(int id);
}
