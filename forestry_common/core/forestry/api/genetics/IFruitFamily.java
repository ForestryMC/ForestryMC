/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.genetics;

public interface IFruitFamily {

	/**
	 * @return Unique String identifier.
	 */
	String getUID();

	/**
	 * @return Localized family name for user display.
	 */
	String getName();

	/**
	 * A scientific-y name for this fruit family
	 * 
	 * @return flavour text (may be null)
	 */
	String getScientific();

	/**
	 * @return Localized description of this fruit family. (May be null.)
	 */
	String getDescription();

}
