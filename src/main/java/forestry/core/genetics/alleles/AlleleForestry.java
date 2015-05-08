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
package forestry.core.genetics.alleles;

import org.apache.commons.lang3.text.WordUtils;

import forestry.core.utils.StringUtil;

public class AlleleForestry extends Allele {
	public AlleleForestry(String prefix, String name, boolean isDominant) {
		this(prefix, name, isDominant, false);
	}

	protected AlleleForestry(String prefix, String name, boolean isDominant, boolean skipRegister) {
		super("forestry." + prefix + WordUtils.capitalize(name), isDominant, skipRegister);
		setName(prefix, name);
	}

	private AlleleForestry setName(String customPrefix, String name) {
		String customName = "gui." + customPrefix + '.' + name;
		if (StringUtil.canTranslate(customName)) {
			this.name = customName;
		} else {
			this.name = "gui." + name;
		}
		return this;
	}
}
