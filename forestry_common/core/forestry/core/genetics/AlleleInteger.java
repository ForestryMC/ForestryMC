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

import forestry.api.genetics.IAlleleInteger;

public class AlleleInteger extends Allele implements IAlleleInteger {

	int value;

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

}
