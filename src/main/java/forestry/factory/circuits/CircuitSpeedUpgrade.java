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
package forestry.factory.circuits;

import forestry.core.circuits.Circuit;
import forestry.core.circuits.ISpeedUpgradable;

public class CircuitSpeedUpgrade extends Circuit {

	private final double speedBoost;
	private final float powerDraw;

	public CircuitSpeedUpgrade(String uid, double speedBoost, float powerDraw, int limit) {
		super(uid, false);
		this.setLimit(limit);
		this.speedBoost = speedBoost;
		this.powerDraw = powerDraw;
	}

	@Override
	public boolean isCircuitable(Object tile) {
		return tile instanceof ISpeedUpgradable;
	}

	@Override
	public void onInsertion(int slot, Object tile) {
		if (!isCircuitable(tile)) {
			return;
		}
		if (tile instanceof ISpeedUpgradable) {
			ISpeedUpgradable machine = (ISpeedUpgradable) tile;
			machine.applySpeedUpgrade(speedBoost, powerDraw);
		}
	}

	@Override
	public void onLoad(int slot, Object tile) {
		onInsertion(slot, tile);
	}

	@Override
	public void onRemoval(int slot, Object tile) {
		if (!isCircuitable(tile)) {
			return;
		}
		if (tile instanceof ISpeedUpgradable) {
			ISpeedUpgradable machine = (ISpeedUpgradable) tile;
			machine.applySpeedUpgrade(-speedBoost, -powerDraw);
		}
	}

	@Override
	public void onTick(int slot, Object tile) {
	}
}
