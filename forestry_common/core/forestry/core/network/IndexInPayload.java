/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.network;

/**
 * Keeps track of the indices to use when writing data to payload arrays. Internal use only.
 */
public class IndexInPayload {
	public IndexInPayload(int intIndex, int floatIndex, int stringIndex) {
		this.intIndex = intIndex;
		this.floatIndex = floatIndex;
		this.stringIndex = stringIndex;
	}

	public int intIndex = 0;
	public int floatIndex = 0;
	public int stringIndex = 0;
}
