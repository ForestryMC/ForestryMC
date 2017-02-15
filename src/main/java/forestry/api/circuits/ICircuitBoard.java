/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.circuits;

import java.util.List;

import forestry.api.core.INbtWritable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICircuitBoard extends INbtWritable {
	@SideOnly(Side.CLIENT)
	int getPrimaryColor();

	@SideOnly(Side.CLIENT)
	int getSecondaryColor();

	@SideOnly(Side.CLIENT)
	void addTooltip(List<String> list);

	void onInsertion(Object tile);

	void onLoad(Object tile);

	void onRemoval(Object tile);

	void onTick(Object tile);

	ICircuit[] getCircuits();

	/**
	 * Specifies where a circuit can be used.
	 */
	ICircuitSocketType getSocketType();

}
