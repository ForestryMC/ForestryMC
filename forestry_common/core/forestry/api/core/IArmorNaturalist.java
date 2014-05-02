/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IArmorNaturalist {

	/**
	 * Called when the naturalist's armor acts as spectacles for seeing pollinated tree leaves/flowers.
	 * 
	 * @param player
	 *            Player doing the viewing
	 * @param armor
	 *            Armor item
	 * @param doSee
	 *            Whether or not to actually do the side effects of viewing
	 * @return true if the armor actually allows the player to see pollination.
	 */
	public boolean canSeePollination(EntityPlayer player, ItemStack armor, boolean doSee);
}
