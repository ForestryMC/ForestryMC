package forestry.food;

import forestry.api.food.IBeverageEffect;

public class BeverageEffects {
	public static final IBeverageEffect weakAlcoholic = new BeverageEffectDrunk(10, 0.2f);
	public static final IBeverageEffect weakAntidote = new BeverageEffectAntidote(20, 0.5f);
	public static final IBeverageEffect strongAntidote = new BeverageEffectAntidote(21, 1.0f);

	private BeverageEffects() {

	}
}
