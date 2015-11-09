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

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.GuiHandlerBase;
import forestry.core.config.ForestryItem;
import forestry.core.network.GuiId;
import forestry.core.tiles.TileUtil;
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
import forestry.mail.inventory.ItemInventoryLetter;
import forestry.mail.items.ItemLetter;
import forestry.mail.tiles.TileMailbox;
import forestry.mail.tiles.TilePhilatelist;
import forestry.mail.tiles.TileTrader;

public class GuiHandlerMail extends GuiHandlerBase {

	@SideOnly(Side.CLIENT)
	@Override
	public Gui getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[id]) {
			case CatalogueGUI:
				ItemStack cata = player.getCurrentEquippedItem();
				if (cata == null) {
					return null;
				}

				if (ForestryItem.catalogue.isItemEqual(cata)) {
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
					return new GuiLetter(player, new ItemInventoryLetter(player, equipped));
				} else {
					return null;
				}

			case MailboxGUI:
				return new GuiMailbox(player.inventory, TileUtil.getTile(world, x, y, z, TileMailbox.class));
			case PhilatelistGUI:
				return new GuiPhilatelist(player.inventory, TileUtil.getTile(world, x, y, z, TilePhilatelist.class));
			case TraderGUI:
				return new GuiTrader(player.inventory, TileUtil.getTile(world, x, y, z, TileTrader.class));
			case TraderNameGUI:
				return new GuiTradeName(TileUtil.getTile(world, x, y, z, TileTrader.class));
			default:
				return null;

		}
	}

	@Override
	public Container getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[id]) {
			case CatalogueGUI:
				ItemStack cata = player.getCurrentEquippedItem();
				if (cata == null) {
					return null;
				}

				if (ForestryItem.catalogue.isItemEqual(cata)) {
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
					return new ContainerLetter(player, new ItemInventoryLetter(player, equipped));
				} else {
					return null;
				}

			case MailboxGUI:
				return new ContainerMailbox(player.inventory, TileUtil.getTile(world, x, y, z, TileMailbox.class));
			case PhilatelistGUI:
				return new ContainerPhilatelist(player.inventory, TileUtil.getTile(world, x, y, z, TilePhilatelist.class));
			case TraderGUI:
				return new ContainerTrader(player.inventory, TileUtil.getTile(world, x, y, z, TileTrader.class));
			case TraderNameGUI:
				return new ContainerTradeName(TileUtil.getTile(world, x, y, z, TileTrader.class));
			default:
				return null;

		}
	}

}
