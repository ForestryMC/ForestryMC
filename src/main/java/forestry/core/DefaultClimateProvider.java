package forestry.core;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IClimateProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class DefaultClimateProvider implements IClimateProvider {
    private World world;
    private BlockPos pos;

    public DefaultClimateProvider(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    @Override
    public Biome getBiome() {
        return world.getBiome(pos);
    }

    @Override
    public EnumTemperature getTemperature() {
        return EnumTemperature.getFromBiome(world.getBiome(pos), world, pos);
    }

    @Override
    public EnumHumidity getHumidity() {
        return EnumHumidity.getFromValue(ForestryAPI.climateManager.getHumidity(world, pos));
    }
}
