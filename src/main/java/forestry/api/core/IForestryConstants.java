/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.LootTableLoadEvent;

public interface IForestryConstants {

	/**
	 * @return The villager ID for the Apiarist Villager.
	 */
	String getApicultureVillagerID();

	/**
	 * @return The villager ID for the Arborist Villager.
	 */
	String getArboricultureVillagerID();

	/**
	 * @return The {@link LootTableLoadEvent} key for adding items to the Forestry Villager chest.
	 */
	ResourceLocation getVillagerChestLootKey();
}
