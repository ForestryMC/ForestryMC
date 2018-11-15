package forestry.arboriculture.genetics;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import forestry.api.arboriculture.IGrowthProvider;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;

public class ClimateGrowthProvider implements IGrowthProvider {

	@Nullable
	private EnumTemperature temperature;
	@Nullable
	private EnumHumidity humidity;
	private final EnumTolerance temperatureTolerance;
	private final EnumTolerance humidityTolerance;

	public ClimateGrowthProvider(EnumTemperature temperature, EnumTolerance temperatureTolerance, EnumHumidity humidity, EnumTolerance humidityTolerance) {
		this.temperature = temperature;
		this.temperatureTolerance = temperatureTolerance;
		this.humidity = humidity;
		this.humidityTolerance = humidityTolerance;
	}

	public ClimateGrowthProvider() {
		this.temperature = null;
		this.temperatureTolerance = EnumTolerance.NONE;
		this.humidity = null;
		this.humidityTolerance = EnumTolerance.NONE;
	}

	@Override
	public boolean canSpawn(ITree tree, World world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean isBiomeValid(ITree tree, Biome biome) {
		EnumTemperature biomeTemperature = EnumTemperature.getFromBiome(biome);
		EnumHumidity biomeHumidity = EnumHumidity.getFromValue(biome.getRainfall());
		ITreeGenome genome = tree.getGenome();
		if (temperature == null) {
			temperature = genome.getPrimary().getTemperature();
		}
		if (humidity == null) {
			humidity = genome.getPrimary().getHumidity();
		}
		return AlleleManager.climateHelper.isWithinLimits(biomeTemperature, biomeHumidity, temperature, temperatureTolerance, humidity, humidityTolerance);
	}

}
