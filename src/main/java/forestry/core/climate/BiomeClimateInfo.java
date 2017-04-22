package forestry.core.climate;

import java.util.HashMap;
import java.util.Map;

import forestry.api.climate.IClimateInfo;
import net.minecraft.world.biome.Biome;

public class BiomeClimateInfo implements IClimateInfo {

	private static final Map<Biome, BiomeClimateInfo> climateControls = new HashMap<>();

	public static BiomeClimateInfo getInfo(Biome biome) {
		if (!climateControls.containsKey(biome)) {
			climateControls.put(biome, new BiomeClimateInfo(biome));
		}
		return climateControls.get(biome);
	}

	private final Biome biome;

	private BiomeClimateInfo(Biome biome) {
		this.biome = biome;
	}

	@Override
	public float getTemperature() {
		return biome.getTemperature();
	}

	@Override
	public float getHumidity() {
		return biome.getRainfall();
	}
}
