package forestry.api.core;

import net.minecraft.world.biome.BiomeGenBase;

public interface IClimateProvider {

    BiomeGenBase getBiome();

    EnumTemperature getTemperature();

    EnumHumidity getHumidity();
}
