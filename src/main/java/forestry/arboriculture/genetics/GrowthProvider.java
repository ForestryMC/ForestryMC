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
package forestry.arboriculture.genetics;

import javax.annotation.Nullable;

import forestry.api.arboriculture.IGrowthProvider;
import forestry.api.arboriculture.ITreeGenome;
import forestry.core.utils.Translator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @deprecated this is being removed to simplify trees
 */
@Deprecated
public class GrowthProvider implements IGrowthProvider {

	@Override
	@Nullable
	public BlockPos canGrow(ITreeGenome genome, World world, BlockPos pos, int expectedGirth, int expectedHeight) {
		return TreeGrowthHelper.canGrow(world, genome, pos, expectedGirth, expectedHeight);
	}

	@Override
	public String getDescription() {
		return Translator.translateToLocal("for.growth.normal");
	}

	@Override
	public String[] getInfo() {
		return new String[0];
	}
}
