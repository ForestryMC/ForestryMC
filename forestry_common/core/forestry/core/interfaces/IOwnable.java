/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.interfaces;

import net.minecraft.entity.player.EntityPlayer;

import com.mojang.authlib.GameProfile;

import forestry.core.utils.EnumAccess;

public interface IOwnable {

	boolean isOwnable();

	boolean isOwned();

	GameProfile getOwnerName();

	void setOwner(EntityPlayer player);

	boolean isOwner(EntityPlayer player);

	boolean switchAccessRule(EntityPlayer player);

	EnumAccess getAccess();

	boolean allowsRemoval(EntityPlayer player);

	boolean allowsInteraction(EntityPlayer player);
}
