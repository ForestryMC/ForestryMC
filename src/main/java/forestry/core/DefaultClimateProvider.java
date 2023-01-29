package forestry.core;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IClimateProvider;

public class DefaultClimateProvider implements IClimateProvider {

    private World world;
    private int xCoord, yCoord, zCoord;

    public DefaultClimateProvider(World world, int x, int y, int z) {
        this.world = world;
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
    }

    @Override
    public BiomeGenBase getBiome() {
        return world.getBiomeGenForCoords(xCoord, zCoord);
    }

    @Override
    public EnumTemperature getTemperature() {
        return EnumTemperature.getFromBiome(getBiome(), xCoord, yCoord, zCoord);
    }

    @Override
    public EnumHumidity getHumidity() {
        return EnumHumidity.getFromValue(ForestryAPI.climateManager.getHumidity(world, xCoord, yCoord, zCoord));
    }
}
