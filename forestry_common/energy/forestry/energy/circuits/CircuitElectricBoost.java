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
package forestry.energy.circuits;

public class CircuitElectricBoost extends CircuitElectricChange {

	public CircuitElectricBoost(String uid, int limit, int eu, int mj, String name, String[] description) {
		super(uid, false, name, description);
		this.setLimit(limit);
		this.configureChange(eu, mj);
	}

}
