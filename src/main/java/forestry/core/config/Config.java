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
package forestry.core.config;

import com.google.common.collect.LinkedListMultimap;

import forestry.mail.gui.GuiMailboxInfo;

public class Config {

	public static final boolean isDebug = false;

	// Graphics
	public static final boolean enableParticleFX = true;

	// Humus
	public static final int humusDegradeDelimiter = 3;

	// Climatology
	public static final int habitatformerRange = 10;
	public static final float habitatformerAreaCostModifier = 0.5F;
	public static final float habitatformerAreaSpeedModifier = 0.5F;

	// Genetics
	public static boolean pollinateVanillaTrees = true;
	public static int analyzerEnergyPerWork = 20320;
	public static float researchMutationBoostMultiplier = 1.5f;
	public static float maxResearchMutationBoostPercent = 5.0f;

	public static final float generateBeehivesAmount = 1.0f;
	public static final boolean generateBeehivesDebug = false;
	public static final boolean logHivePlacement = false;
	public static final boolean logCocoonPlacement = false;
	public static final boolean logTreePlacement = false;
	public static final boolean enableVillagers = true;

	// Performance
	public static final boolean enableBackpackResupply = true;

	// Farm
	public static final int farmSize = 2;
	public static final float fertilizerModifier = 1.0F;
	public static final boolean squareFarms = false;

	// Cultivation
	public static final int planterExtend = 4;
	public static final boolean ringFarms = true;
	public static final int ringSize = 4;

	// Mail
	public static final boolean mailAlertEnabled = true;
	public static final GuiMailboxInfo.XPosition mailAlertXPosition = GuiMailboxInfo.XPosition.LEFT;
	public static final GuiMailboxInfo.YPosition mailAlertYPosition = GuiMailboxInfo.YPosition.TOP;

	public static final boolean craftingStampsEnabled = true;

	// Fluids
	public static final boolean CapsuleFluidPickup = false;
	public static final boolean nonConsumableCapsules = false;

	// Gui tabs (Ledger)
	public static final int guiTabSpeed = 8;

	// Hints
	public static final boolean enableHints = true;
	public static final LinkedListMultimap<String, String> hints = LinkedListMultimap.create();
	public static final boolean enableEnergyStat = true;

	// Energy
	public static final EnergyDisplayMode energyDisplayMode = EnergyDisplayMode.RF;

	// Charcoal
	public static final int charcoalAmountBase = 8;
	public static final int charcoalWallCheckRange = 16;
}
