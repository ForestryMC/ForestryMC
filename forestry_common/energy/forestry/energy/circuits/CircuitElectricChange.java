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
import forestry.energy.gadgets.EngineTin;

public abstract class CircuitElectricChange extends Circuit {

	int euChange = 7;
	int mjChange = 2;

	public CircuitElectricChange(String uid, boolean requiresDiscovery, String name, String[] descriptions) {
		super(uid, requiresDiscovery, name, descriptions);
	}

	protected void configureChange(int euChange, int mjChange) {
		this.euChange = euChange;
		this.mjChange = mjChange;
	}

	@Override
	public boolean isCircuitable(TileEntity tile) {
		return tile instanceof EngineTin;
	}

	@Override
	public void onInsertion(int slot, TileEntity tile) {
		if (!isCircuitable(tile))
			return;

		EngineTin engine = (EngineTin) tile;
		engine.changeEnergyConfig(euChange, mjChange, 2 * euChange);
	}

	@Override
	public void onLoad(int slot, TileEntity tile) {
		onInsertion(slot, tile);
	}

	@Override
	public void onRemoval(int slot, TileEntity tile) {
		if (!isCircuitable(tile))
			return;

		EngineTin engine = (EngineTin) tile;
		engine.changeEnergyConfig(-euChange, -mjChange, -(2 * euChange));
	}

	@Override
	public void onTick(int slot, TileEntity tile) {
	}
}
