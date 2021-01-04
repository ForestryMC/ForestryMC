/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

public interface IClimateRoot {

    /**
     * @return the listener at the given position in the given world if there is any.
     */
    LazyOptional<IClimateListener> getListener(World world, BlockPos pos);

    /**
     * Can be used to get the climate state without the use of an {@link IClimateListener}.
     *
     * @return The climate state at the given location.
     */
    IClimateState getState(World world, BlockPos pos);

    /**
     * @return The climate of the biome at the given position contained in a {@link IClimateState}.
     */
    IClimateState getBiomeState(World worldObj, BlockPos coordinates);

    /**
     * @return Create a climate provider.
     */
    IClimateProvider getDefaultClimate(World world, BlockPos pos);

    /**
     * @return The climate holder of the given world.
     */
    IWorldClimateHolder getWorldClimate(World world);
}
