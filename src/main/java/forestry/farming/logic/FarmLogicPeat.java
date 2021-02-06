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
package forestry.farming.logic;

import java.util.Collection;
import java.util.Stack;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmProperties;
import forestry.core.features.CoreBlocks;
import forestry.farming.logic.crops.CropPeat;

public class FarmLogicPeat extends FarmLogicWatered {
	public FarmLogicPeat(IFarmProperties properties, boolean isManual) {
		super(properties, isManual);
	}

	@Override
	public Collection<ICrop> harvest(World world, IFarmHousing housing, FarmDirection direction, int extent, BlockPos pos) {
		Stack<ICrop> crops = new Stack<>();
		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(pos, direction, i);
			if (!world.isBlockLoaded(position)) {
				return crops;
			}
			BlockState blockState = world.getBlockState(position);
			if (CoreBlocks.PEAT.blockEqual(blockState)) {
				crops.push(new CropPeat(world, position));
			}
		}
		return crops;
	}
}
