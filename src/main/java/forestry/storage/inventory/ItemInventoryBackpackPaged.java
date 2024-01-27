/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.storage.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.network.NetworkHooks;

import forestry.core.gui.IPagedInventory;
import forestry.storage.items.ItemBackpackNaturalist;

public class ItemInventoryBackpackPaged extends ItemInventoryBackpack implements IPagedInventory {
	private final ItemBackpackNaturalist backpackNaturalist;

	public ItemInventoryBackpackPaged(Player player, int size, ItemStack itemstack, ItemBackpackNaturalist backpackNaturalist) {
		super(player, size, itemstack);
		this.backpackNaturalist = backpackNaturalist;
	}

	//TODO gui
	@Override
	public void flipPage(ServerPlayer player, short page) {
		ItemStack heldItem = player.getItemInHand(player.getUsedItemHand());
		NetworkHooks.openScreen(player, new ItemBackpackNaturalist.ContainerProvider(heldItem), b -> {

		});
	}
}
