/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.circuits;

import java.util.List;

import forestry.api.core.INBTTagable;

public interface ICircuitBoard extends INBTTagable {

	int getPrimaryColor();

	int getSecondaryColor();

	void addTooltip(List<String> list);

	void onInsertion(Object tile);

	void onLoad(Object tile);

	void onRemoval(Object tile);

	void onTick(Object tile);
	
	ICircuit[] getCircuits();

	/**
	 * Specifies where a circuit can be used.
	 * @since Forestry 4.0
	 */
	ICircuitSocketType getSocketType();

}
