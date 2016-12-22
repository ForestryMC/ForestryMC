package forestry.core.climate;

import java.util.HashMap;
import java.util.Map;

import forestry.greenhouse.multiblock.DefaultClimateControl;
import net.minecraft.world.biome.Biome;

public class BiomeClimateControl extends DefaultClimateControl {

	private static final Map<Biome, BiomeClimateControl> climateControls = new HashMap<>();

	public static BiomeClimateControl getControl(Biome biome) {
		if (!climateControls.containsKey(biome)) {
			climateControls.put(biome, new BiomeClimateControl(biome));
		}
		return climateControls.get(biome);
	}

	private final Biome biome;

	private BiomeClimateControl(Biome biome) {
		this.biome = biome;
	}

	@Override
	public float getControlTemperature() {
		return biome.getTemperature();
	}

	@Override
	public float getControlHumidity() {
		return biome.getRainfall();
	}

}
