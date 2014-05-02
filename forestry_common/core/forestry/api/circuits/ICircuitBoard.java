/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.circuits;

import java.util.List;

import net.minecraft.tileentity.TileEntity;

import forestry.api.core.INBTTagable;

public interface ICircuitBoard extends INBTTagable {

	int getPrimaryColor();

	int getSecondaryColor();

	void addTooltip(List<String> list);

	void onInsertion(TileEntity tile);

	void onLoad(TileEntity tile);

	void onRemoval(TileEntity tile);

	void onTick(TileEntity tile);
	
	ICircuit[] getCircuits();

}
