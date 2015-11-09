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
package forestry.arboriculture;

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.arboriculture.TreeManager;
import forestry.arboriculture.gui.GuiTreealyzer;
import forestry.arboriculture.inventory.ItemInventoryTreealyzer;
import forestry.core.GuiHandlerBase;
import forestry.core.gui.ContainerAlyzer;
import forestry.core.network.GuiId;

public class GuiHandlerArboriculture extends GuiHandlerBase {

	@Override
	public Container getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int cleanId = decodeGuiID(id);

		if (cleanId >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[cleanId]) {

			case ArboristChestGUI:
				return this.getNaturalistChestContainer(TreeManager.treeRoot, player, world, x, y, z, decodeGuiData(id));

			case TreealyzerGUI:
				ItemStack equipped = player.getCurrentEquippedItem();
				if (equipped == null) {
					return null;
				}

				return new ContainerAlyzer(new ItemInventoryTreealyzer(player, equipped), player);

			default:
				return null;

		}

	}

	@SideOnly(Side.CLIENT)
	@Override
	public Gui getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int cleanId = decodeGuiID(id);
		
		if (cleanId >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[cleanId]) {

			case ArboristChestGUI:
				return this.getNaturalistChestGui(TreeManager.treeRoot, player, world, x, y, z, decodeGuiData(id));

			case TreealyzerGUI:
				ItemStack equipped = player.getCurrentEquippedItem();
				if (equipped == null) {
					return null;
				}

				return new GuiTreealyzer(player, new ItemInventoryTreealyzer(player, equipped));

			default:
				return null;

		}

	}
}
