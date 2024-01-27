package forestry.core.config;

import java.text.NumberFormat;

import net.minecraft.client.Minecraft;

public enum EnergyDisplayMode {
	RF;

	EnergyDisplayMode() {
	}

	public String formatEnergyValue(int energy) {
		return NumberFormat.getIntegerInstance(Minecraft.getInstance().getLocale()).format((float) energy) + " RF";
	}
}
