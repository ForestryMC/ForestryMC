/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser Public License v3 which accompanies this distribution, and is available
 * at http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to: SirSengir (original work), CovertJaguar, Player, Binnie,
 * MysteriousAges
 ******************************************************************************/
package forestry.apiculture.worldgen;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class HiveGenGround extends HiveGen {

    private final Set<Material> groundMaterials = new HashSet<>();

    public HiveGenGround(Block... groundBlocks) {
        for (Block block : groundBlocks) {
            groundMaterials.add(block.getMaterial());
        }
    }

    @Override
    public boolean isValidLocation(World world, int x, int y, int z) {
        Block ground = world.getBlock(x, y - 1, z);
        return groundMaterials.contains(ground.getMaterial());
    }

    @Override
    public int getYForHive(World world, int x, int z) {
        int y = world.getHeightValue(x, z);

        // get to the ground
        while (y >= 0 && (world.getBlock(x, y - 1, z).isLeaves(world, x, y - 1, z) || canReplace(world, x, y - 1, z))) {
            y--;
        }

        return y;
    }
}
