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
package forestry.factory.triggers;

import forestry.core.triggers.Trigger;

public class FactoryTriggers {
	public static Trigger lowResource25;
	public static Trigger lowResource10;
	public static Trigger lowFuel25;
	public static Trigger lowFuel10;

	public static void initialize() {
		lowResource25 = new TriggerLowResource("lowResources.25", 0.25f);
		lowResource10 = new TriggerLowResource("lowResources.10", 0.1f);
		lowFuel25 = new TriggerLowFuel("lowFuel.25", 0.25f);
		lowFuel10 = new TriggerLowFuel("lowFuel.10", 0.1f);
	}
}
