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
package forestry.mail.triggers;

import forestry.core.triggers.Trigger;

public class MailTriggers {
	public static Trigger triggerHasMail;
	public static Trigger lowPaper64;
	public static Trigger lowPaper32;
	public static Trigger lowPostage40;
	public static Trigger lowPostage20;
	public static Trigger lowInput25;
	public static Trigger lowInput10;
	public static Trigger highBuffer75;
	public static Trigger highBuffer90;

	public static void initialize() {
		// triggerHasMail = new TriggerHasMail();
		lowPaper64 = new TriggerLowPaper("mail.lowPaper.64", 64);
		lowPaper32 = new TriggerLowPaper("mail.lowPaper.32", 32);
		lowPostage40 = new TriggerLowStamps("mail.lowStamps.40", 40);
		lowPostage20 = new TriggerLowStamps("mail.lowStamps.20", 20);
		lowInput25 = new TriggerLowInput("mail.lowInput.25", 0.25f);
		lowInput10 = new TriggerLowInput("mail.lowInput.10", 0.1f);
		highBuffer75 = new TriggerBuffer("mail.lowBuffer.75", 0.75f);
		highBuffer90 = new TriggerBuffer("mail.lowBuffer.90", 0.90f);
	}
}
