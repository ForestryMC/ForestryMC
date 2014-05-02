/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.storage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

import cpw.mods.fml.common.eventhandler.Event;


public abstract class BackpackEvent extends Event {

	public final EntityPlayer player;
	public final IBackpackDefinition backpackDefinition;
	public final IInventory backpackInventory;

	public BackpackEvent(EntityPlayer player, IBackpackDefinition backpackDefinition, IInventory backpackInventory) {
		this.player = player;
		this.backpackDefinition = backpackDefinition;
		this.backpackInventory = backpackInventory;
	}
}
