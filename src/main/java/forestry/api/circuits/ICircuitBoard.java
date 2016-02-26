/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.circuits;

import javax.annotation.Nonnull;
import java.util.List;

import forestry.api.core.INbtWritable;

public interface ICircuitBoard extends INbtWritable {

	int getPrimaryColor();

	int getSecondaryColor();

	void addTooltip(List<String> list);

	void onInsertion(Object tile);

	void onLoad(Object tile);

	void onRemoval(Object tile);

	void onTick(Object tile);

	@Nonnull
	ICircuit[] getCircuits();

	/**
	 * Specifies where a circuit can be used.
	 */
	@Nonnull
	ICircuitSocketType getSocketType();

}
