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
package forestry.core.climate;

import forestry.api.climate.EnumClimatiserModes;
import forestry.api.climate.EnumClimatiserTypes;
import forestry.api.climate.IClimatiserDefinition;

public class ClimatiserDefinition implements IClimatiserDefinition {

	protected final float change;
	protected final EnumClimatiserModes mode;
	protected final float range;
	protected final EnumClimatiserTypes type;

	public ClimatiserDefinition(float change, EnumClimatiserModes mode, float range, EnumClimatiserTypes type) {
		this.change = change;
		this.mode = mode;
		this.range = range;
		this.type = type;
	}

	@Override
	public float getChange() {
		return change;
	}

	@Override
	public float getRange() {
		return range;
	}

	@Override
	public EnumClimatiserTypes getType() {
		return type;
	}

	@Override
	public EnumClimatiserModes getMode() {
		return mode;
	}

}
