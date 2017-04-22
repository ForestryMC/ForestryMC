/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/**
 * Helper class for checking whether an entity is wearing Apiarist Armor
 *
 * @author Vexatos
 */
public interface IArmorApiaristHelper {

	/**
	 * Called when the apiarist's armor acts as protection against an attack.
	 *
	 * @param stack     ItemStack to check
	 * @param entity    Entity being attacked
	 * @param cause     Optional cause of attack, such as a bee effect identifier
	 * @param doProtect Whether or not to actually do the side effects of protection
	 * @return Whether or not the item is valid Apiarist Armor and should protect the player from that attack
	 * @since Forestry 4.2
	 */
	boolean isArmorApiarist(ItemStack stack, EntityLivingBase entity, String cause, boolean doProtect);

	/**
	 * Called when the apiarist's armor acts as protection against an attack.
	 *
	 * @param entity    Entity being attacked
	 * @param cause     Optional cause of attack, such as a bee effect identifier
	 * @param doProtect Whether or not to actually do the side effects of protection
	 * @return The number of valid Apiarist Armor pieces the player is wearing that are actually protecting.
	 * 4 means full protection, but it can go higher if they are holding items like the smoker.
	 * @since Forestry 4.2
	 */
	int wearsItems(EntityLivingBase entity, String cause, boolean doProtect);
}
