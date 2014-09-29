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
package forestry.core.genetics;

import forestry.api.genetics.IAlleleFloat;
import forestry.core.utils.StringUtil;

public class AlleleFloat extends Allele implements IAlleleFloat {

	float value;

	public AlleleFloat(String uid, float value) {
		this(uid, value, false);
	}

	public AlleleFloat(String uid, float value, boolean isDominant) {
		super(uid, isDominant);
		this.value = value;
	}

	@Override
	public float getValue() {
		return value;
	}

	public AlleleFloat setName(String customPrefix, String name) {
		String customName = "gui." + customPrefix + "." + name;
		if (StringUtil.canTranslate(customName))
			this.name = customName;
		else
			this.name = "gui." + name;
		return this;
	}
}
