/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.genetics;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;

import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;

/**
 * Hermits will not produce if there are any other living creatures nearby.
 */
public class JubilanceProviderHermit extends JubilanceDefault {

	@Override
	public boolean isJubilant(IAlleleBeeSpecies species, IBeeGenome genome, IBeeHousing housing) {
		AxisAlignedBB bounding = this.getBounding(genome, housing, 1.0f);

		@SuppressWarnings("rawtypes")
		List list = housing.getWorld().getEntitiesWithinAABB(EntityLiving.class, bounding);
		if (list.size() > 0)
			return false;

		return true;
	}

}
