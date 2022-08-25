/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.world.World;

public interface IClimateManager {

    float getTemperature(World world, int x, int y, int z);

    float getHumidity(World world, int x, int y, int z);

    IClimateProvider getDefaultClimate(World world, int x, int y, int z);
}
