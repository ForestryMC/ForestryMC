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
import forestry.core.utils.BlockUtil;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
//import forestry.plugins.PluginExtraUtilities;

public class FarmLogicRedOrchid extends FarmLogicHomogeneous {

    public FarmLogicRedOrchid(IFarmProperties properties, boolean isManual) {
        super(properties, isManual);
    }

    @Override
    protected boolean maintainSeedlings(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
        for (int i = 0; i < extent; i++) {
            BlockPos position = translateWithOffset(pos, direction, i);
            if (!world.isBlockLoaded(position)) {
                break;
            }

            BlockState state = world.getBlockState(position);
            if (!world.isAirBlock(position) && !BlockUtil.isReplaceableBlock(state, world, position)) {
                continue;
            }

            BlockPos soilPos = position.down();
            BlockState blockState = world.getBlockState(soilPos);
            if (!isAcceptedSoil(blockState)) {
                continue;
            }

            return trySetCrop(world, farmHousing, position, direction);
        }

        return false;
    }
}
