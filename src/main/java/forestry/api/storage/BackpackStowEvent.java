/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.storage;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Use @SubscribeEvent on a method taking this event as an argument. Will fire whenever a backpack tries to store an item. Processing will stop if the stacksize
 * of stackToStow drops to 0 or less or the event is canceled.
 */
@Cancelable
public class BackpackStowEvent extends BackpackEvent {

	public final ItemStack stackToStow;

	public BackpackStowEvent(Player player, IBackpackDefinition backpackDefinition, Container backpackInventory, ItemStack stackToStow) {
		super(player, backpackDefinition, backpackInventory);
		this.stackToStow = stackToStow;
	}
}
