/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import forestry.api.arboriculture.ArboricultureCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Naturalist armor allows players to see pollinated tree leaves.
 *
 * @see ArboricultureCapabilities#ARMOR_NATURALIST
 */
public interface IArmorNaturalist {

	/**
	 * Called to see if this naturalist's armor allows for seeing pollinated tree leaves/flowers.
	 *
	 * @param player Player doing the viewing
	 * @param armor  Armor item
	 * @param doSee  Whether or not to actually do the side effects of viewing
	 * @return true if the armor actually allows the player to see pollination.
	 */
	boolean canSeePollination(EntityPlayer player, ItemStack armor, boolean doSee);
}
