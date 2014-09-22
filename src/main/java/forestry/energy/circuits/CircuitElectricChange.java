/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.energy.circuits;

import net.minecraft.tileentity.TileEntity;

import forestry.core.circuits.Circuit;
import forestry.energy.gadgets.EngineTin;

public abstract class CircuitElectricChange extends Circuit {

	int euChange = 7;
	int rfChange = 20;

	public CircuitElectricChange(String uid, boolean requiresDiscovery) {
		super(uid, requiresDiscovery);
	}

	protected void configureChange(int euChange, int rfChange) {
		this.euChange = euChange;
		this.rfChange = rfChange;
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
		engine.changeEnergyConfig(euChange, rfChange, 2 * euChange);
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
		engine.changeEnergyConfig(-euChange, -rfChange, -(2 * euChange));
	}

	@Override
	public void onTick(int slot, TileEntity tile) {
	}
}
