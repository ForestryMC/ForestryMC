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
package forestry.core.triggers;

import buildcraft.api.statements.StatementManager;

public class ForestryTrigger {

	public static Trigger lowFuel25;
	public static Trigger lowFuel10;
	public static Trigger lowResource25;
	public static Trigger lowResource10;
	public static Trigger lowSoil25;
	public static Trigger lowSoil10;
	public static Trigger lowGermlings25;
	public static Trigger lowGermlings10;
	public static Trigger missingQueen;
	public static Trigger missingDrone;
	public static Trigger hasWork;

	public static void initialize() {
		StatementManager.registerStatement(lowFuel25 = new TriggerLowFuel("lowFuel.25", 0.25f));
		StatementManager.registerStatement(lowFuel10 = new TriggerLowFuel("lowFuel.10", 0.1f));
		StatementManager.registerStatement(lowResource25 = new TriggerLowResource("lowResources.25", 0.25f));
		StatementManager.registerStatement(lowResource10 = new TriggerLowResource("lowResources.10", 0.1f));

		StatementManager.registerStatement(lowSoil25 = new TriggerLowSoil("lowSoil.25", 0.25f));
		StatementManager.registerStatement(lowSoil10 = new TriggerLowSoil("lowSoil.10", 0.1f));
		StatementManager.registerStatement(lowGermlings25 = new TriggerLowGermlings("lowGermlings.25", 0.25f));
		StatementManager.registerStatement(lowGermlings10 = new TriggerLowGermlings("lowGermlings.10", 0.1f));

		StatementManager.registerStatement(missingQueen = new TriggerMissingQueen());
		StatementManager.registerStatement(missingDrone = new TriggerMissingDrone());

		StatementManager.registerStatement(hasWork = new TriggerHasWork());

	}
}
