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
package forestry.farming.triggers;

import java.util.Arrays;
import java.util.List;

import forestry.core.triggers.Trigger;

import buildcraft.api.statements.ITriggerExternal;

public class FarmingTriggers {
	public static Trigger lowResourceLiquid50;
	public static Trigger lowResourceLiquid25;
	public static Trigger lowSoil128;
	public static Trigger lowSoil64;
	public static Trigger lowSoil32;
	public static Trigger lowFertilizer50;
	public static Trigger lowFertilizer25;
	public static Trigger lowGermlings25;
	public static Trigger lowGermlings10;
	public static List<ITriggerExternal> allExternalTriggers;

	public static void initialize() {
		allExternalTriggers = Arrays.<ITriggerExternal>asList(
				lowResourceLiquid50 = new TriggerLowLiquid("lowLiquid.50", 0.5f),
				lowResourceLiquid25 = new TriggerLowLiquid("lowLiquid.25", 0.25f),
				lowSoil128 = new TriggerLowSoil(128),
				lowSoil64 = new TriggerLowSoil(64),
				lowSoil32 = new TriggerLowSoil(32),
				lowFertilizer50 = new TriggerLowFertilizer("lowFertilizer.50", 0.5f),
				lowFertilizer25 = new TriggerLowFertilizer("lowFertilizer.25", 0.25f),
				lowGermlings25 = new TriggerLowGermlings("lowGermlings.25", 0.25f),
				lowGermlings10 = new TriggerLowGermlings("lowGermlings.10", 0.1f)
		);
	}
}
