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

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmable;
import forestry.core.utils.BlockUtil;

public class FarmLogicCrops extends FarmLogicWatered {

    public FarmLogicCrops(IFarmProperties properties, boolean isManual) {
        super(properties, isManual);
    }

    @Override
    public NonNullList<ItemStack> collect(World world, IFarmHousing farmHousing) {
        return collectEntityItems(world, farmHousing, false);
    }

    @Override
    protected boolean maintainCrops(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
        for (int i = 0; i < extent; i++) {
            BlockPos position = translateWithOffset(pos, direction, i);
            if (!world.isBlockLoaded(position)) {
                break;
            }

            BlockState state = world.getBlockState(position);
            if (!world.isAirBlock(position) && !BlockUtil.isReplaceableBlock(state, world, position)) {
                continue;
            }

            BlockState groundState = world.getBlockState(position.down());
            if (isAcceptedSoil(groundState)) {
                return trySetCrop(world, farmHousing, position, direction);
            }
        }

        return false;
    }

    private boolean trySetCrop(World world, IFarmHousing farmHousing, BlockPos position, FarmDirection direction) {
        for (IFarmable candidate : getFarmables()) {
            if (farmHousing.plantGermling(candidate, world, position, direction)) {
                return true;
            }
        }

        return false;
    }
}
