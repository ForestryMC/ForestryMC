/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.genetics;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IFruitFamily;
import forestry.core.utils.StringUtil;

public class FruitFamily implements IFruitFamily {

	private String uid;
	private String scientific;

	public FruitFamily(String uid, String scientific) {
		this.uid = uid;
		this.scientific = scientific;
		AlleleManager.alleleRegistry.registerFruitFamily(this);
	}

	@Override
	public String getUID() {
		return "forestry." + uid;
	}

	@Override
	public String getScientific() {
		return this.scientific;
	}

	@Override
	public String getName() {
		return StringUtil.localize("family." + uid);
	}

	@Override
	public String getDescription() {
		return null;
	}

}
