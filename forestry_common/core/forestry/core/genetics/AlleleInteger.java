/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
