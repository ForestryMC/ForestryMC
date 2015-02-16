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

import forestry.api.genetics.IAlleleInteger;
import forestry.core.utils.StringUtil;

public class AlleleInteger extends Allele implements IAlleleInteger {

	private final int value;

	public AlleleInteger(String uid, int value) {
		this(uid, value, false);
	}

	public AlleleInteger(String uid, int value, boolean isDominant) {
		super(uid, isDominant);
		this.value = value;
	}

	@Override
	public int getValue() {
		return value;
	}

	public AlleleInteger setName(String customPrefix, String name) {
		String customName = "gui." + customPrefix + "." + name;
		if (StringUtil.canTranslate(customName)) {
			this.name = customName;
		} else {
			this.name = "gui." + name;
		}
		return this;
	}

}
