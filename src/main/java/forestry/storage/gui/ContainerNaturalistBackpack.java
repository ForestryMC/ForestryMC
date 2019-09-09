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

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import forestry.core.config.Constants;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.ContainerNaturalistInventory;
import forestry.core.gui.IGuiSelectable;
import forestry.storage.ModuleBackpacks;
import forestry.storage.inventory.ItemInventoryBackpackPaged;
import forestry.storage.items.ItemBackpackNaturalist;

public class ContainerNaturalistBackpack extends ContainerItemInventory<ItemInventoryBackpackPaged> implements IGuiSelectable {

	public ContainerNaturalistBackpack(int windowId, PlayerInventory inv, ItemInventoryBackpackPaged inventory, int selectedPage) {
		super(windowId, inventory, inv, 18, 120, ModuleBackpacks.getContainerTypes().NATURALIST_BACKPACK);

		ContainerNaturalistInventory.addInventory(this, inventory, selectedPage);
	}

	@Override
	public void handleSelectionRequest(ServerPlayerEntity player, int primary, int secondary) {
		inventory.flipPage(player, (short) primary);
	}

	public static ContainerNaturalistBackpack fromNetwork(int windowId, PlayerInventory playerInventory, PacketBuffer extraData) {
		ItemStack parent = extraData.readItemStack();
		ItemBackpackNaturalist backpack = (ItemBackpackNaturalist) extraData.readItemStack().getItem();    //TODO this is b it ugly
		ItemInventoryBackpackPaged paged = new ItemInventoryBackpackPaged(playerInventory.player, Constants.SLOTS_BACKPACK_APIARIST, parent, backpack);
		int page = extraData.readVarInt();
		return new ContainerNaturalistBackpack(windowId, playerInventory, paged, page);
	}
}
