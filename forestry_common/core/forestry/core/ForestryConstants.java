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
package forestry.core;

import forestry.api.core.IForestryConstants;
import forestry.core.config.Defaults;

public class ForestryConstants implements IForestryConstants {

	@Override
	public int getApicultureVillagerID() {
		return Defaults.ID_VILLAGER_BEEKEEPER;
	}

	@Override
	public int getArboricultureVillagerID() {
		return Defaults.ID_VILLAGER_LUMBERJACK;
	}

	@Override
	public String getVillagerChestGenKey() {
		return Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST;
	}

}
