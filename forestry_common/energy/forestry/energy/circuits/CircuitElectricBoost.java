/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.energy.circuits;

public class CircuitElectricBoost extends CircuitElectricChange {

	public CircuitElectricBoost(String uid, int limit, int eu, int mj, String name, String[] description) {
		super(uid, false, name, description);
		this.setLimit(limit);
		this.configureChange(eu, mj);
	}

}
