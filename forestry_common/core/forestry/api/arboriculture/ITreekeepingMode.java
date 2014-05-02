/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.arboriculture;

import java.util.ArrayList;

public interface ITreekeepingMode extends ITreeModifier {

	/**
	 * @return Localized name of this treekeeping mode.
	 */
	String getName();

	/**
	 * @return Localized list of strings outlining the behaviour of this treekeeping mode.
	 */
	ArrayList<String> getDescription();

}
