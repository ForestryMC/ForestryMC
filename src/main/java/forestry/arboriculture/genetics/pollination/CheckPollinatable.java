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
package forestry.arboriculture.genetics.pollination;

import java.util.EnumSet;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;

public class CheckPollinatable implements ICheckPollinatable {

	private final IPollinatable pollinatable;

	public CheckPollinatable(IPollinatable pollinatable) {
		this.pollinatable = pollinatable;
	}

	@Override
	public EnumSet<EnumPlantType> getPlantType() {
		return pollinatable.getPlantType();
	}

	@Override
	public IIndividual getPollen() {
		return pollinatable.getPollen();
	}

	@Override
	public boolean canMateWith(IIndividual pollen) {
		return pollinatable.canMateWith(pollen);
	}

	@Override
	public boolean isPollinated() {
		return pollinatable.isPollinated();
	}
}
