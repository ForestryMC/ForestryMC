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
package forestry.greenhouse.circuits;

import javax.annotation.Nullable;

import forestry.api.climate.ClimateType;
import forestry.core.circuits.Circuit;
import forestry.greenhouse.api.climate.IClimateSource;
import forestry.greenhouse.api.climate.IClimateSourceCircuitable;
import forestry.greenhouse.api.climate.IClimateSourceOwner;

public class CircuitClimateSource extends Circuit {
	private ClimateType type;
	private float changeChange;
	private float rangeChange;
	private float energyChange;

	public CircuitClimateSource(String uid, ClimateType type, float changeChange, float rangeChange, float energyChange) {
		super(uid);
		this.type = type;
		this.changeChange = changeChange;
		this.rangeChange = rangeChange;
		this.energyChange = energyChange;
	}

	@Override
	public boolean isCircuitable(Object tile) {
		return tile instanceof IClimateSourceOwner && ((IClimateSourceOwner) tile).isCircuitable();
	}

	@Override
	public void onInsertion(int slot, Object tile) {
		IClimateSourceOwner owner = getCircuitable(tile);
		if (owner == null) {
			return;
		}
		IClimateSource source = owner.getClimateSource();
		if (!(source instanceof IClimateSourceCircuitable)) {
			return;
		}
		((IClimateSourceCircuitable) source).changeSourceConfig(type, changeChange, rangeChange, energyChange);
	}

	@Override
	public void onLoad(int slot, Object tile) {
		onInsertion(slot, tile);
	}

	@Override
	public void onRemoval(int slot, Object tile) {
		IClimateSourceOwner owner = getCircuitable(tile);
		if (owner == null) {
			return;
		}
		IClimateSource source = owner.getClimateSource();
		if (!(source instanceof IClimateSourceCircuitable)) {
			return;
		}
		((IClimateSourceCircuitable) source).changeSourceConfig(type, -changeChange, -rangeChange, -energyChange);
	}

	@Override
	public void onTick(int slot, Object tile) {

	}

	@Nullable
	private IClimateSourceOwner getCircuitable(Object tile) {
		if (!isCircuitable(tile)) {
			return null;
		}
		return (IClimateSourceOwner) tile;
	}
}
