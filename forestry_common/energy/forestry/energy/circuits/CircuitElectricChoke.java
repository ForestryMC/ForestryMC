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

public class CircuitElectricChoke extends CircuitElectricChange {

	public CircuitElectricChoke(String uid) {
		super(uid, false, "electric.choke.1", new String[] { "Reduces ouput by 1 MJ/t", "Reduces intake by 2 EU/t" });
		this.configureChange(-2, -1);
		this.setLimit(1);
	}
}
