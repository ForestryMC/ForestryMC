package forestry.api.core;

import net.minecraft.world.biome.Biome;

public interface IClimateProvider {
    Biome getBiome();

    EnumTemperature getTemperature();

    EnumHumidity getHumidity();
}
