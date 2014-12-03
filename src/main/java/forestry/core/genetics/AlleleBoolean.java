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

import forestry.api.genetics.IAlleleBoolean;

public class AlleleBoolean extends Allele implements IAlleleBoolean {

	final boolean value;

	public AlleleBoolean(String uid, boolean value) {
		this(uid, value, false);
	}

	public AlleleBoolean(String uid, boolean value, boolean isDominant) {
		super(uid, isDominant);
		this.value = value;
	}

	public boolean getValue() {
		return value;
	}

}
