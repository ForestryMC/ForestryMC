package forestry.core.config;

import net.minecraftforge.client.MinecraftForgeClient;

import java.text.NumberFormat;
import java.util.Locale;

public enum EnergyDisplayMode {
	RF("RF", 1),
	FE("FE", 1),
	MJ("MJ", 0.1f),
	TESLA("T", 1);

	private final String energyName;
	private final float factor;

	EnergyDisplayMode(String energyName, float factor) {
		this.energyName = energyName;
		this.factor = factor;
	}

	public String formatRate(int energy) {
		String amountString = formatEnergyNum(energy);
		return String.format("%s %s/t", amountString, energyName);
	}

	public String formatEnergyValue(int energy) {
		String amountString = formatEnergyNum(energy);
		return String.format("%s %s", amountString, energyName);
	}

	private String formatEnergyNum(int energy) {
		Locale locale = MinecraftForgeClient.getLocale();
		NumberFormat numberFormat = NumberFormat.getIntegerInstance(locale);
		float amount = energy * factor;
		return numberFormat.format(amount);
	}
}
