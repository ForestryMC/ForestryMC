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

public class CircuitElectricEfficiency extends CircuitElectricChange {

	public CircuitElectricEfficiency(String uid) {
		super(uid, false, "electric.efficiency.1", new String[] { "Reduces intake by 1 EU/t" });
		this.setLimit(1);
		this.configureChange(-1, 0);
	}

}
