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
package forestry.mail.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import forestry.core.gui.ContainerTile;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.proxy.Proxies;
import forestry.core.utils.SlotUtil;
import forestry.mail.POBox;
import forestry.mail.POBoxInfo;
import forestry.mail.network.packets.PacketPOBoxInfoUpdate;
import forestry.mail.tiles.TileMailbox;

public class ContainerMailbox extends ContainerTile<TileMailbox> {

	public static final short SLOT_LETTERS = 0;
	public static final short SLOT_LETTERS_COUNT = 7 * 12;

	private final POBox mailInventory;

	public ContainerMailbox(InventoryPlayer playerInventory, TileMailbox tile) {
		super(tile, playerInventory, 35, 145);
		IInventory inventory = tile.getOrCreateMailInventory(playerInventory.player.worldObj, playerInventory.player.getGameProfile());

		if (inventory instanceof POBox) {
			this.mailInventory = (POBox) inventory;
		} else {
			this.mailInventory = null;
		}

		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 12; j++) {
				addSlotToContainer(new SlotOutput(inventory, j + i * 9, 8 + j * 18, 8 + i * 18));
			}
		}
	}

	@Override
	public ItemStack slotClick(int slotIndex, int button, int par3, EntityPlayer player) {
		ItemStack stack = super.slotClick(slotIndex, button, par3, player);

		if (SlotUtil.isSlotInRange(slotIndex, SLOT_LETTERS, SLOT_LETTERS_COUNT)) {
			if (!player.worldObj.isRemote && mailInventory != null) {
				POBoxInfo info = mailInventory.getPOBoxInfo();
				Proxies.net.sendToPlayer(new PacketPOBoxInfoUpdate(info), player);
			}
		}

		return stack;
	}
}
