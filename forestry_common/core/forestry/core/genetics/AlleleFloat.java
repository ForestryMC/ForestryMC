/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.genetics;

import forestry.api.genetics.IAlleleFloat;

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

}
