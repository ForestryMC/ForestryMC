package forestry.arboriculture.genetics;

import forestry.api.arboriculture.IGrowthProvider;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.core.utils.ClimateUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClimateGrowthProvider implements IGrowthProvider {

	private EnumTemperature temperature;
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
	public boolean canSpawn(ITreeGenome genome, World world, BlockPos pos) {
		EnumTemperature biomeTemperature = EnumTemperature.getFromBiome(world.getBiome(pos), world, pos);
		EnumHumidity biomeHumidity = EnumHumidity.getFromValue(ClimateUtil.getHumidity(world, pos));
		if(temperature == null){
			temperature = genome.getPrimary().getTemperature();
		}
		if(humidity == null){
			humidity = genome.getPrimary().getHumidity();
		}
		return AlleleManager.climateHelper.isWithinLimits(biomeTemperature, biomeHumidity, temperature, temperatureTolerance, humidity, humidityTolerance);
	}

}
