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

import forestry.api.genetics.IAlleleFloat;

public class AlleleFloat extends AlleleCategorized implements IAlleleFloat {

	private final float value;

	public AlleleFloat(String modId, String category, String valueName, float value, boolean isDominant) {
		super(modId, category, valueName, isDominant);
		this.value = value;
	}

	@Override
	public float getValue() {
		return value;
	}
}
