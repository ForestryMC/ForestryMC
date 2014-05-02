/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.food;

import forestry.api.food.BeverageManager;
import forestry.api.food.IBeverageEffect;
import forestry.core.utils.StringUtil;

public abstract class BeverageEffect implements IBeverageEffect {

	public static final IBeverageEffect weakAlcoholic = new BeverageEffectDrunk(10, 0.2f);
	public static final IBeverageEffect weakAntidote = new BeverageEffectAntidote(20, 0.5f);
	public static final IBeverageEffect strongAntidote = new BeverageEffectAntidote(21, 1.0f);

	private int id;
	protected String description;

	public BeverageEffect(int id) {
		this.id = id;

		if (BeverageManager.effectList[id] != null)
			throw new RuntimeException("Beverage effect slot " + id + " was already occupied by " + BeverageManager.effectList[id].toString()
					+ " when trying to add " + this.toString());
		else
			BeverageManager.effectList[id] = this;
	}

	public int getId() {
		return this.id;
	}

	public String getLevel() {
		return null;
	}

	@Override
	public String getDescription() {
		if (getLevel() != null)
			return StringUtil.localize(description) + " " + getLevel();
		else
			return StringUtil.localize(description);
	}
}
