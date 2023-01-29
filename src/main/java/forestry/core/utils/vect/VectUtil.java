/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser Public License v3 which accompanies this distribution, and is available
 * at http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to: SirSengir (original work), CovertJaguar, Player, Binnie,
 * MysteriousAges
 ******************************************************************************/
package forestry.core.utils.vect;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public final class VectUtil {

    public static boolean isAirBlock(World world, IVect position) {
        return world.isAirBlock(position.getX(), position.getY(), position.getZ());
    }

    public static boolean isWoodBlock(World world, IVect position) {
        Block block = getBlock(world, position);
        return block.isWood(world, position.getX(), position.getY(), position.getZ());
    }

    public static TileEntity getTile(World world, IVect position) {
        return world.getTileEntity(position.getX(), position.getY(), position.getZ());
    }

    public static Block getBlock(World world, IVect position) {
        return world.getBlock(position.getX(), position.getY(), position.getZ());
    }

    public static int getBlockMeta(World world, IVect position) {
        return world.getBlockMetadata(position.getX(), position.getY(), position.getZ());
    }

    public static ItemStack getAsItemStack(World world, IVect position) {
        return new ItemStack(getBlock(world, position), 1, getBlockMeta(world, position));
    }
}
