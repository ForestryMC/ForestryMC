/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.storage;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;

import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Use @SubscribeEvent on a method taking this event as an argument. Will fire whenever a backpack tries to resupply to a player inventory. Processing will stop
 * if the event is canceled.
 */
@Cancelable
public class BackpackResupplyEvent extends BackpackEvent {

	public BackpackResupplyEvent(Player player, IBackpackDefinition backpackDefinition, Container backpackInventory) {
		super(player, backpackDefinition, backpackInventory);
	}

}
