package forestry.core.config;

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
		return String.format("%.0f %s/t", energy * factor, energyName);
	}

	public String formatEnergyValue(int energy) {
		return String.format("%.0f %s", energy * factor, energyName);
	}
}
