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

public interface ICircuit {
	String getUID();

	boolean requiresDiscovery();

	int getLimit();

	String getName();

	boolean isCircuitable(TileEntity tile);

	void onInsertion(int slot, TileEntity tile);

	void onLoad(int slot, TileEntity tile);

	void onRemoval(int slot, TileEntity tile);

	void onTick(int slot, TileEntity tile);

	void addTooltip(List<String> list);
}
