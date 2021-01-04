package forestry.core.items;

public class DrinkProperties {
	private final int healAmount;
	private final int maxItemUseDuration;
	private final float saturationModifier;

	public DrinkProperties(int healAmount, float saturationModifier, int maxItemUseDuration) {
		this.healAmount = healAmount;
		this.saturationModifier = saturationModifier;
		this.maxItemUseDuration = maxItemUseDuration;
	}

	public int getHealAmount() {
		return healAmount;
	}

	public float getSaturationModifier() {
		return saturationModifier;
	}

	public int getMaxItemUseDuration() {
		return maxItemUseDuration;
	}
}
