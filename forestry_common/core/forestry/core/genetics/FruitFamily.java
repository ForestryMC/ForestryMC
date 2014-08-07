/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
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
