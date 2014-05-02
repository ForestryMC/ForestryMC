/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.triggers;

import buildcraft.api.gates.ActionManager;

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
		ActionManager.registerTrigger(lowFuel25 = new TriggerLowFuel("lowFuel.25", 0.25f));
		ActionManager.registerTrigger(lowFuel10 = new TriggerLowFuel("lowFuel.10", 0.1f));
		ActionManager.registerTrigger(lowResource25 = new TriggerLowResource("lowResources.25", 0.25f));
		ActionManager.registerTrigger(lowResource10 = new TriggerLowResource("lowResources.10", 0.1f));

		ActionManager.registerTrigger(lowSoil25 = new TriggerLowSoil("lowSoil.25", 0.25f));
		ActionManager.registerTrigger(lowSoil10 = new TriggerLowSoil("lowSoil.10", 0.1f));
		ActionManager.registerTrigger(lowGermlings25 = new TriggerLowGermlings("lowGermlings.25", 0.25f));
		ActionManager.registerTrigger(lowGermlings10 = new TriggerLowGermlings("lowGermlings.10", 0.1f));

		ActionManager.registerTrigger(missingQueen = new TriggerMissingQueen());
		ActionManager.registerTrigger(missingDrone = new TriggerMissingDrone());

		ActionManager.registerTrigger(hasWork = new TriggerHasWork());

	}
}
