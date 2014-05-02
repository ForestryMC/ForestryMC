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
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.eventhandler.Cancelable;

/**
 * Use @SubscribeEvent on a method taking this event as an argument. Will fire whenever a backpack tries to store an item. Processing will stop if the stacksize
 * of stackToStow drops to 0 or less or the event is canceled.
 */
@Cancelable
public class BackpackStowEvent extends BackpackEvent {

	public final ItemStack stackToStow;

	public BackpackStowEvent(EntityPlayer player, IBackpackDefinition backpackDefinition, IInventory backpackInventory, ItemStack stackToStow) {
		super(player, backpackDefinition, backpackInventory);
		this.stackToStow = stackToStow;
	}
}
