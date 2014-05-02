/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.genetics;

import forestry.api.genetics.IAlleleBoolean;

public class AlleleBoolean extends Allele implements IAlleleBoolean {

	boolean value;
	boolean isDominant;

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
