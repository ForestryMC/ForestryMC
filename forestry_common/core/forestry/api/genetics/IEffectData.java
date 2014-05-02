/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.genetics;

import forestry.api.core.INBTTagable;

/**
 * Container to hold some temporary data for bee, tree and butterfly effects.
 * 
 * @author SirSengir
 */
public interface IEffectData extends INBTTagable {
	void setInteger(int index, int val);

	void setFloat(int index, float val);

	void setBoolean(int index, boolean val);

	int getInteger(int index);

	float getFloat(int index);

	boolean getBoolean(int index);
}
