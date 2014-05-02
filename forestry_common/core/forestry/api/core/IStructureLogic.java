/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.core;

public interface IStructureLogic extends INBTTagable {

	/**
	 * @return String unique to the type of structure controlled by this structure logic.
	 */
	String getTypeUID();

	/**
	 * Called by {@link ITileStructure}'s validateStructure().
	 */
	void validateStructure();

}
