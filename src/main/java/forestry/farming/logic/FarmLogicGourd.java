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

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmable;
import forestry.core.utils.BlockUtil;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FarmLogicGourd extends FarmLogicWatered {

    public FarmLogicGourd(IFarmProperties properties, boolean isManual) {
        super(properties, isManual);
    }

    @Override
    protected boolean maintainCrops(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
        for (int i = 0; i < extent; i++) {
            BlockPos position = translateWithOffset(pos, direction, i);
            if (!world.isBlockLoaded(position)) {
                break;
            }

            BlockState state = world.getBlockState(position);
            if (!world.isAirBlock(position) && !BlockUtil.isReplaceableBlock(state, world, position)
                    || !isValidPosition(farmHousing, direction, position, CultivationType.CROP)) {
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

    @Override
    protected boolean isValidPosition(IFarmHousing housing, FarmDirection direction, BlockPos position, CultivationType type) {
        BlockPos farmLocation = housing.getFarmCorner(direction).offset(direction.getFacing());
        int xVal = farmLocation.getX() & 1;
        int zVal = farmLocation.getZ() & 1;
        boolean uneven = ((position.getX() & 1) != xVal) ^ ((position.getZ() & 1) != zVal);
        return (type == CultivationType.WATER) != uneven;
    }
}
