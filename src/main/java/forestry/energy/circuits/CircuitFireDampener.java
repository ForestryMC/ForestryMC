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

import forestry.core.circuits.Circuit;
import forestry.energy.gadgets.EngineCopper;

public class CircuitFireDampener extends Circuit {

	public CircuitFireDampener(String uid) {
		super(uid, false);
	}

	@Override
	public boolean isCircuitable(Object tile) {
		return tile instanceof EngineCopper;
	}

	@Override
	public void onInsertion(int slot, Object tile) {
		if (!isCircuitable(tile)) {
			return;
		}

	}

	@Override
	public void onLoad(int slot, Object tile) {
		onInsertion(slot, tile);
	}

	@Override
	public void onRemoval(int slot, Object tile) {
	}

	@Override
	public void onTick(int slot, Object tile) {
	}

}
