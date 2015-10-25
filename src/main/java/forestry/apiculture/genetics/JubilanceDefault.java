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
package forestry.apiculture.genetics;

import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IJubilanceProvider;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;

public class JubilanceDefault implements IJubilanceProvider {

	public static final JubilanceDefault instance = new JubilanceDefault();

	protected JubilanceDefault() {

	}

	@Override
	public boolean isJubilant(IAlleleBeeSpecies species, IBeeGenome genome, IBeeHousing housing) {
		EnumTemperature temperature = housing.getTemperature();
		EnumHumidity humidity = housing.getHumidity();

		return temperature == species.getTemperature() && humidity == species.getHumidity();
	}

}
