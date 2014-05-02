/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.energy.circuits;

import net.minecraft.tileentity.TileEntity;

import forestry.core.circuits.Circuit;
import forestry.energy.gadgets.EngineCopper;

public class CircuitFireDampener extends Circuit {

	public CircuitFireDampener(String uid) {
		super(uid, false, "dampener.1", new String[] { "Reduces maximum output to 1 MJ/t", "Preserves fuel output" });
	}

	@Override
	public boolean isCircuitable(TileEntity tile) {
		return tile instanceof EngineCopper;
	}

	@Override
	public void onInsertion(int slot, TileEntity tile) {
		if (!isCircuitable(tile))
			return;

	}

	@Override
	public void onLoad(int slot, TileEntity tile) {
		onInsertion(slot, tile);
	}

	@Override
	public void onRemoval(int slot, TileEntity tile) {
	}

	@Override
	public void onTick(int slot, TileEntity tile) {
	}

}
