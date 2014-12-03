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

import forestry.api.mail.IMailAddress;
import forestry.api.mail.PostManager;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.slots.SlotClosed;
import forestry.core.proxy.Proxies;
import forestry.mail.POBox;
import forestry.mail.gadgets.MachineMailbox;
import forestry.plugins.PluginMail;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMailbox extends ContainerForestry {

	public static final short SLOT_LETTERS = 0;
	public static final short SLOT_LETTERS_COUNT = 7 * 12;

	private final MachineMailbox mailbox;
	private final POBox mailInventory;

	public ContainerMailbox(InventoryPlayer player, MachineMailbox tile) {
		super(tile);

		// Mailbox contents
		this.mailbox = tile;

		IInventory inv = mailbox.getOrCreateMailInventory(player.player.worldObj, player.player.getGameProfile());
		if (inv instanceof POBox)
			this.mailInventory = (POBox) inv;
		else
			this.mailInventory = null;

		for (int i = 0; i < 7; i++)
			for (int j = 0; j < 12; j++)
				addSlot(new SlotClosed(inv, j + i * 9, 8 + j * 18, 8 + i * 18));

		// Player inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				addSlot(new Slot(player, j + i * 9 + 9, 35 + j * 18, 145 + i * 18));
		// Player hotbar
		for (int i = 0; i < 9; i++)
			addSlot(new Slot(player, i, 35 + i * 18, 203));

	}

	@Override
	public ItemStack slotClick(int slotIndex, int button, int par3, EntityPlayer player) {
		ItemStack stack = super.slotClick(slotIndex, button, par3, player);

		if (slotIndex >= SLOT_LETTERS && slotIndex < SLOT_LETTERS + SLOT_LETTERS_COUNT) {
			if (Proxies.common.isSimulating(player.worldObj) && mailInventory != null) {
				IMailAddress address = PostManager.postRegistry.getMailAddress(player.getGameProfile());
				PluginMail.proxy.setPOBoxInfo(mailbox.getWorldObj(), address, mailInventory.getPOBoxInfo());
			}
		}

		return stack;
	}
}
