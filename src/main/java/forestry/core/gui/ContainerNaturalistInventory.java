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
package forestry.core.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

import forestry.core.gui.slots.SlotFiltered;
import forestry.core.network.PacketGuiSelectRequest;
import forestry.core.tiles.TileNaturalistChest;

public class ContainerNaturalistInventory extends ContainerTile<TileNaturalistChest> implements IGuiSelectable {

	public ContainerNaturalistInventory(InventoryPlayer player, TileNaturalistChest tile, int page) {
		super(tile, player, 18, 120);

		// Inventory
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 5; y++) {
				addSlotToContainer(new SlotFiltered(tile, y + page * 25 + x * 5, 100 + y * 18, 21 + x * 18));
			}
		}
	}

	@Override
	public void handleSelectionRequest(EntityPlayerMP player, PacketGuiSelectRequest packet) {
		tile.flipPage(player, packet.getPrimaryIndex());
	}

	@Override
	public void addCraftingToCrafters(ICrafting p_75132_1_) {
		super.addCraftingToCrafters(p_75132_1_);
		tile.increaseNumPlayersUsing();
	}

	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		tile.decreaseNumPlayersUsing();
	}
}
