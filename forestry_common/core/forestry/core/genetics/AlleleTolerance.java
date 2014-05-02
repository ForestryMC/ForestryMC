/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.genetics;

import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAlleleTolerance;

public class AlleleTolerance extends Allele implements IAlleleTolerance {

	EnumTolerance value;

	public AlleleTolerance(String uid, EnumTolerance value) {
		this(uid, value, false);
	}

	public AlleleTolerance(String uid, EnumTolerance value, boolean isDominant) {
		super(uid, isDominant);
		this.value = value;
	}

	public EnumTolerance getValue() {
		return value;
	}
	
	public String getName() {
		switch(value) {
		case BOTH_1:
			return "Both 1";
		case BOTH_2:
			return "Both 2";
		case BOTH_3:
			return "Both 3";
		case BOTH_4:
			return "Both 4";
		case BOTH_5:
			return "Both 5";
		case DOWN_1:
			return "Down 1";
		case DOWN_2:
			return "Down 2";
		case DOWN_3:
			return "Down 3";
		case DOWN_4:
			return "Down 4";
		case DOWN_5:
			return "Down 5";
		case NONE:
			return "None";
		case UP_1:
			return "Up 1";
		case UP_2:
			return "Up 2";
		case UP_3:
			return "Up 3";
		case UP_4:
			return "Up 4";
		case UP_5:
			return "Up 5";
		default:
			return "";
		
		}
	}

}
