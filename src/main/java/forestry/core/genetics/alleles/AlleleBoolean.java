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

import forestry.api.genetics.IAlleleBoolean;

public class AlleleBoolean extends AlleleCategorized implements IAlleleBoolean {

	private final boolean value;

	public AlleleBoolean(String modId, String category, boolean value, boolean isDominant) {
		super(modId, category, Boolean.toString(value), isDominant);
		this.value = value;
	}

	@Override
	public boolean getValue() {
		return value;
	}

}
