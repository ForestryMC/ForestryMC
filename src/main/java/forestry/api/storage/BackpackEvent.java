/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.storage;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;

import net.minecraftforge.eventbus.api.Event;

public abstract class BackpackEvent extends Event {

	public final Player player;
	public final IBackpackDefinition backpackDefinition;
	public final Container backpackInventory;

	public BackpackEvent(Player player, IBackpackDefinition backpackDefinition, Container backpackInventory) {
		this.player = player;
		this.backpackDefinition = backpackDefinition;
		this.backpackInventory = backpackInventory;
	}
}
