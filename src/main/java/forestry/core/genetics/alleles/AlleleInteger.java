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

import forestry.api.genetics.IAlleleInteger;

public class AlleleInteger extends AlleleForestry implements IAlleleInteger {

	private final int value;

	public AlleleInteger(String prefix, String name, int value) {
		this(prefix, name, value, false);
	}

	public AlleleInteger(String prefix, String name, int value, boolean isDominant) {
		super(prefix, name, isDominant);
		this.value = value;
	}

	@Override
	public int getValue() {
		return value;
	}
}
