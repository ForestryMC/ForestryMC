/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser Public License v3 which accompanies this distribution, and is available
 * at http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to: SirSengir (original work), CovertJaguar, Player, Binnie,
 * MysteriousAges
 ******************************************************************************/
package forestry.core;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import forestry.api.core.IClimateManager;
import forestry.api.core.IClimateProvider;

public class ClimateManager implements IClimateManager {

    @Override
    public float getTemperature(World world, int x, int y, int z) {
        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        return biome.temperature;
    }

    @Override
    public float getHumidity(World world, int x, int y, int z) {
        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        return biome.rainfall;
    }

    @Override
    public IClimateProvider getDefaultClimate(World world, int x, int y, int z) {
        return new DefaultClimateProvider(world, x, y, z);
    }
}
