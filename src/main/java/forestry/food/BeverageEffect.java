/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.food;

import forestry.api.food.BeverageManager;
import forestry.api.food.IBeverageEffect;
import forestry.core.utils.StringUtil;

public abstract class BeverageEffect implements IBeverageEffect {

	public static final IBeverageEffect weakAlcoholic = new BeverageEffectDrunk(10, 0.2f);
	public static final IBeverageEffect weakAntidote = new BeverageEffectAntidote(20, 0.5f);
	public static final IBeverageEffect strongAntidote = new BeverageEffectAntidote(21, 1.0f);

	private final int id;
	protected String description;

	public BeverageEffect(int id) {
		this.id = id;

		if (BeverageManager.effectList[id] != null) {
			throw new RuntimeException("Beverage effect slot " + id + " was already occupied by " + BeverageManager.effectList[id].toString()
					+ " when trying to add " + this.toString());
		} else {
			BeverageManager.effectList[id] = this;
		}
	}

	public int getId() {
		return this.id;
	}

	public String getLevel() {
		return null;
	}

	@Override
	public String getDescription() {
		if (getLevel() != null) {
			return StringUtil.localize(description) + " " + getLevel();
		} else {
			return StringUtil.localize(description);
		}
	}
}
