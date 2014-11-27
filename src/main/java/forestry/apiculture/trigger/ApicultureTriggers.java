/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.apiculture.trigger;

import forestry.core.triggers.Trigger;

public class ApicultureTriggers {
	public static Trigger noFrames;
	public static Trigger missingQueen;
	public static Trigger missingDrone;

	public static void initialize() {
		noFrames = new TriggerNoFrames();

		missingQueen = new TriggerMissingQueen();
		missingDrone = new TriggerMissingDrone();
	}
}
