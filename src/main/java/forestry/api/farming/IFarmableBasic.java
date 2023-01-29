/*******************************************************************************
 * Copyright 2022, glee8e
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import net.minecraft.block.Block;
import net.minecraft.world.World;

/**
 * A basic form of {@link IFarmable}. It adds one more variant of isSapling, allowing much better runtime performance
 * than otherwise.
 */
public interface IFarmableBasic extends IFarmable {

    /**
     * @return true if the block at the given location is a "sapling" for this type, i.e. a non-harvestable immature
     *         version of the crop.
     */
    default boolean isSaplingAt(World world, int x, int y, int z) {
        return world.blockExists(x, y, z)
                && isSapling(world.getBlock(x, y, z), isMetadataAware() ? world.getBlockMetadata(x, y, z) : 0);
    }

    /**
     * @return true if given block at the given location is a "sapling" for this type, i.e. a non-harvestable immature
     *         version of the crop.
     */
    boolean isSapling(Block block, int meta);

    default boolean isMetadataAware() {
        return false;
    }
}
