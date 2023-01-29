/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser Public License v3 which accompanies this distribution, and is available
 * at http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to: SirSengir (original work), CovertJaguar, Player, Binnie,
 * MysteriousAges
 ******************************************************************************/
package forestry.apiculture.worldgen;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class HiveGenTree extends HiveGen {

    @Override
    public boolean isValidLocation(World world, int x, int y, int z) {
        Block blockAbove = world.getBlock(x, y + 1, z);
        if (!blockAbove.isLeaves(world, x, y + 1, z)) {
            return false;
        }

        // not a good location if right on top of something
        return canReplace(world, x, y - 1, z);
    }

    @Override
    public int getYForHive(World world, int x, int z) {
        // get top leaf block
        int y = world.getHeightValue(x, z) - 1;
        if (!world.getBlock(x, y, z).isLeaves(world, x, y, z)) {
            return -1;
        }

        // get to the bottom of the leaves
        do {
            y--;
        } while (world.getBlock(x, y, z).isLeaves(world, x, y, z));

        return y;
    }
}
