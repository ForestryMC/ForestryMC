/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http:www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.farming.logic;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmProperties;
import forestry.farming.logic.crops.CropRubber;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Collections;
import java.util.Stack;
//import forestry.plugins.PluginIC2;
//import forestry.plugins.PluginTechReborn;

public class FarmLogicRubber extends FarmLogic {

    private final boolean active = true;

    public FarmLogicRubber(IFarmProperties properties, boolean isManual) {
        super(properties, isManual);
        //		if ((PluginIC2.rubberWood == null || PluginIC2.resin == null) &&
        //			PluginTechReborn.rubberItemsSuccess()) {
        //			Log.warning("Failed to init a farm logic {} since IC2 rubber wood or resin were not found", getClass().getName());
        //			active = false;
        //		}
    }

    @Override
    public Collection<ICrop> harvest(
            World world,
            IFarmHousing farmHousing,
            FarmDirection direction,
            int extent,
            BlockPos pos
    ) {
        if (!active) {
            return Collections.emptyList();
        }

        BlockPos position = farmHousing.getValidPosition(direction, pos, extent, pos.up());
        Collection<ICrop> crops = getHarvestBlocks(world, position);
        farmHousing.increaseExtent(direction, pos, extent);

        return crops;
    }

    private Collection<ICrop> getHarvestBlocks(World world, BlockPos position) {
        Stack<ICrop> crops = new Stack<>();

        for (int j = 0; j < 10; j++) {
            BlockPos candidate = position.add(0, j, 0);

            if (!world.isBlockLoaded(candidate)) {
                return crops;
            }

            BlockState blockState = world.getBlockState(candidate);
            Block block = blockState.getBlock();
            //			if ((PluginIC2.rubberWood != null && !ItemStackUtil.equals(block, PluginIC2.rubberWood)) &&
            //				(PluginTechReborn.RUBBER_WOOD != null && !ItemStackUtil.equals(block, PluginTechReborn.RUBBER_WOOD))) {
            //				break;
            //			}

            if (CropRubber.hasRubberToHarvest(blockState)) {
                crops.push(new CropRubber(world, blockState, candidate));
                break;
            }
        }

        return crops;
    }

}
