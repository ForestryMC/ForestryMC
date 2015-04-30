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

public class AlleleFloat extends AlleleForestry implements IAlleleFloat {

	private final float value;

	public AlleleFloat(String prefix, String name, float value) {
		this(prefix, name, value, false);
	}

	public AlleleFloat(String prefix, String name, float value, boolean isDominant) {
		super(prefix, name, isDominant);
		this.value = value;
	}

	@Override
	public float getValue() {
		return value;
	}
}
