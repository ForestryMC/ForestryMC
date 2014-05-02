/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.genetics;

import forestry.api.genetics.IAlleleArea;
import forestry.core.utils.Vect;

public class AlleleArea extends Allele implements IAlleleArea {

	private int[] area;

	public AlleleArea(String uid, int[] value) {
		this(uid, value, false);
	}

	public AlleleArea(String uid, int[] value, boolean isDominant) {
		super(uid, isDominant);
		this.area = value;
	}
	
	public int[] getValue() {
		return area;
	}

	public Vect getArea() {
		return new Vect(area);
	}

}
