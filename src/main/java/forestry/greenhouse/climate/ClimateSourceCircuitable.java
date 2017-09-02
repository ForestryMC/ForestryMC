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
package forestry.greenhouse.climate;

import forestry.api.climate.ClimateType;
import forestry.core.climate.ClimateSource;
import forestry.core.climate.ClimateSourceType;
import forestry.greenhouse.api.climate.IClimateSourceCircuitable;
import forestry.greenhouse.api.climate.IClimateSourceOwner;

public abstract class ClimateSourceCircuitable<O extends IClimateSourceOwner> extends ClimateSource<O> implements IClimateSourceCircuitable {
	protected float changeMultiplierTemperature = 1.0f;
	protected float rangeMultiplierTemperature = 1.0f;
	protected float changeMultiplierHumidity = 1.0f;
	protected float rangeMultiplierHumidity = 1.0f;

	public ClimateSourceCircuitable(float change, float range, ClimateSourceType sourceType) {
		super(change, range, sourceType);
	}

	@Override
	public void changeSourceConfig(ClimateType type, float changeChange, float rangeChange) {
		if (type == ClimateType.TEMPERATURE) {
			changeMultiplierTemperature += changeChange;
			rangeMultiplierTemperature += rangeChange;
		} else {
			changeMultiplierHumidity += changeChange;
			rangeMultiplierHumidity += rangeChange;
		}
	}

	public float getChangeMultiplier(ClimateType type) {
		float changeMultiplier;
		if (type == ClimateType.TEMPERATURE) {
			changeMultiplier = changeMultiplierTemperature;
		} else {
			changeMultiplier = changeMultiplierHumidity;
		}
		return changeMultiplier;
	}

	@Override
	protected float getRange(ClimateType type) {
		float rangeMultiplier;
		if (type == ClimateType.TEMPERATURE) {
			rangeMultiplier = rangeMultiplierTemperature;
		} else {
			rangeMultiplier = rangeMultiplierHumidity;
		}
		return super.getRange(type) * rangeMultiplier;
	}

	@Override
	protected float getChange(ClimateType type) {
		return super.getChange(type) * getChangeMultiplier(type);
	}
}
