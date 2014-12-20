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
import java.util.List;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;

/**
 * Hermits will not produce if there are any other living creatures nearby.
 */
public class JubilanceProviderHermit extends JubilanceDefault {

	@Override
	public boolean isJubilant(IAlleleBeeSpecies species, IBeeGenome genome, IBeeHousing housing) {
		AxisAlignedBB bounding = this.getBounding(genome, housing, 1.0f);

		@SuppressWarnings("rawtypes")
		List list = housing.getWorld().getEntitiesWithinAABB(EntityLiving.class, bounding);
		return list.size() <= 0;
	}

}
