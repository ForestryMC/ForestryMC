/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
