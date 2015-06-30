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
package forestry.mail;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.core.GuiHandlerBase;
import forestry.core.network.GuiId;
import forestry.mail.gadgets.MachineMailbox;
import forestry.mail.gadgets.MachinePhilatelist;
import forestry.mail.gadgets.MachineTrader;
import forestry.mail.gui.ContainerCatalogue;
import forestry.mail.gui.ContainerLetter;
import forestry.mail.gui.ContainerMailbox;
import forestry.mail.gui.ContainerPhilatelist;
import forestry.mail.gui.ContainerTradeName;
import forestry.mail.gui.ContainerTrader;
import forestry.mail.gui.GuiCatalogue;
import forestry.mail.gui.GuiLetter;
import forestry.mail.gui.GuiMailbox;
import forestry.mail.gui.GuiPhilatelist;
import forestry.mail.gui.GuiTradeName;
import forestry.mail.gui.GuiTrader;
import forestry.mail.items.ItemCatalogue;
import forestry.mail.items.ItemLetter;
import forestry.mail.items.ItemLetter.LetterInventory;

public class GuiHandlerMail extends GuiHandlerBase {

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[id]) {
			case CatalogueGUI:
				ItemStack cata = player.getCurrentEquippedItem();
				if (cata == null) {
					return null;
				}

				if (cata.getItem() instanceof ItemCatalogue) {
					return new GuiCatalogue(player);
				} else {
					return null;
				}
			
			case LetterGUI:
				ItemStack equipped = player.getCurrentEquippedItem();
				if (equipped == null) {
					return null;
				}

				if (equipped.getItem() instanceof ItemLetter) {
					return new GuiLetter(player, new LetterInventory(player, equipped));
				} else {
					return null;
				}

			case MailboxGUI:
				return new GuiMailbox(player.inventory, getTile(world, x, y, z, player, MachineMailbox.class));
			case PhilatelistGUI:
				return new GuiPhilatelist(player.inventory, getTile(world, x, y, z, player, MachinePhilatelist.class));
			case TraderGUI:
				return new GuiTrader(player.inventory, getTile(world, x, y, z, player, MachineTrader.class));
			case TraderNameGUI:
				return new GuiTradeName(getTile(world, x, y, z, player, MachineTrader.class));
			default:
				return null;

		}
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[id]) {
			case CatalogueGUI:
				ItemStack cata = player.getCurrentEquippedItem();
				if (cata == null) {
					return null;
				}

				if (cata.getItem() instanceof ItemCatalogue) {
					return new ContainerCatalogue(player);
				} else {
					return null;
				}
			
			case LetterGUI:
				ItemStack equipped = player.getCurrentEquippedItem();
				if (equipped == null) {
					return null;
				}

				if (equipped.getItem() instanceof ItemLetter) {
					return new ContainerLetter(player, new LetterInventory(player, equipped));
				} else {
					return null;
				}

			case MailboxGUI:
				return new ContainerMailbox(player.inventory, getTile(world, x, y, z, player, MachineMailbox.class));
			case PhilatelistGUI:
				return new ContainerPhilatelist(player.inventory, getTile(world, x, y, z, player, MachinePhilatelist.class));
			case TraderGUI:
				return new ContainerTrader(player.inventory, getTile(world, x, y, z, player, MachineTrader.class));
			case TraderNameGUI:
				return new ContainerTradeName(getTile(world, x, y, z, player, MachineTrader.class));
			default:
				return null;

		}
	}

}
