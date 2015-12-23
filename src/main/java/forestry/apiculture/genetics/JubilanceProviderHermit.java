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

import java.util.List;

import net.minecraft.entity.EntityLiving;

import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.apiculture.genetics.alleles.AlleleEffect;

/**
 * Hermits will not produce if there are any other living creatures nearby.
 */
public class JubilanceProviderHermit extends JubilanceDefault {
	@Override
	public boolean isJubilant(IAlleleBeeSpecies species, IBeeGenome genome, IBeeHousing housing) {
		List<EntityLiving> list = AlleleEffect.getEntitiesInRange(genome, housing, EntityLiving.class);
		return list.size() <= 0;
	}
}
