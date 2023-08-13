package forestry.core.config;

import java.text.NumberFormat;

import net.minecraftforge.client.MinecraftForgeClient;

public enum EnergyDisplayMode {
	RF;

	EnergyDisplayMode() {
	}

	public String formatEnergyValue(int energy) {
		return NumberFormat.getIntegerInstance(MinecraftForgeClient.getLocale()).format((float) energy) + " RF";
	}
}
