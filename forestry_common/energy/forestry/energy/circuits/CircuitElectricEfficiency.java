/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.energy.circuits;

public class CircuitElectricEfficiency extends CircuitElectricChange {

	public CircuitElectricEfficiency(String uid) {
		super(uid, false, "electric.efficiency.1", new String[] { "Reduces intake by 1 EU/t" });
		this.setLimit(1);
		this.configureChange(-1, 0);
	}

}
