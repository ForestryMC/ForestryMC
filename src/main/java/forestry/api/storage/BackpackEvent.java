/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.storage;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;

import net.minecraftforge.eventbus.api.Event;

public abstract class BackpackEvent extends Event {

	public final PlayerEntity player;
	public final IBackpackDefinition backpackDefinition;
	public final IInventory backpackInventory;

	public BackpackEvent(PlayerEntity player, IBackpackDefinition backpackDefinition, IInventory backpackInventory) {
		this.player = player;
		this.backpackDefinition = backpackDefinition;
		this.backpackInventory = backpackInventory;
	}
}
