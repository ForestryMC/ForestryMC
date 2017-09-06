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

import forestry.api.greenhouse.IClimateHousing;
import forestry.greenhouse.api.climate.IClimateContainer;
import forestry.greenhouse.api.climate.IClimateFactory;
import forestry.greenhouse.climate.ClimateContainer;

public class ClimateFactory implements IClimateFactory{

	@Override
	public IClimateContainer createContainer(IClimateHousing climatedRegion) {
		return new ClimateContainer(climatedRegion);
	}

}
