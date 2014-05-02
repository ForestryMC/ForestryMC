/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.energy.circuits;

public class CircuitElectricChoke extends CircuitElectricChange {

	public CircuitElectricChoke(String uid) {
		super(uid, false, "electric.choke.1", new String[] { "Reduces ouput by 1 MJ/t", "Reduces intake by 2 EU/t" });
		this.configureChange(-2, -1);
		this.setLimit(1);
	}
}
