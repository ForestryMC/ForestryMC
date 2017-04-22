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
package forestry.apiculture.flowers;

import forestry.api.genetics.IFlowerGrowthHelper;
import forestry.api.genetics.IFlowerGrowthRule;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GrowthRuleNone implements IFlowerGrowthRule {

	@Override
	public boolean growFlower(IFlowerGrowthHelper helper, String flowerType, World world, BlockPos pos) {
		return true;
	}

}
