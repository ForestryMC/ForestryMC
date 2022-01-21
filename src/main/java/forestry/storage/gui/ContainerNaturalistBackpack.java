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
package forestry.storage.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;

import forestry.core.config.Constants;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.ContainerNaturalistInventory;
import forestry.core.gui.IGuiSelectable;
import forestry.storage.features.BackpackContainers;
import forestry.storage.inventory.ItemInventoryBackpackPaged;
import forestry.storage.items.ItemBackpackNaturalist;

public class ContainerNaturalistBackpack extends ContainerItemInventory<ItemInventoryBackpackPaged> implements IGuiSelectable {

	public ContainerNaturalistBackpack(int windowId, Inventory inv, ItemInventoryBackpackPaged inventory, int selectedPage) {
		super(windowId, inventory, inv, 18, 120, BackpackContainers.NATURALIST_BACKPACK.containerType());

		ContainerNaturalistInventory.addInventory(this, inventory, selectedPage);
	}

	@Override
	public void handleSelectionRequest(ServerPlayer player, int primary, int secondary) {
		inventory.flipPage(player, (short) primary);
	}

	public static ContainerNaturalistBackpack fromNetwork(int windowId, Inventory playerInventory, FriendlyByteBuf extraData) {
		ItemStack parent = extraData.readItem();
		ItemBackpackNaturalist backpack = (ItemBackpackNaturalist) extraData.readItem().getItem();    //TODO this is b it ugly
		ItemInventoryBackpackPaged paged = new ItemInventoryBackpackPaged(playerInventory.player, Constants.SLOTS_BACKPACK_APIARIST, parent, backpack);
		int page = extraData.readVarInt();
		return new ContainerNaturalistBackpack(windowId, playerInventory, paged, page);
	}
}
