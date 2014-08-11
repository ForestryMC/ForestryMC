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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.arboriculture.genetics.TreeHelper;
import forestry.arboriculture.gui.ContainerTreealyzer;
import forestry.arboriculture.gui.GuiTreealyzer;
import forestry.arboriculture.items.ItemTreealyzer.TreealyzerInventory;
import forestry.core.GuiHandlerBase;
import forestry.core.network.GuiId;

public class GuiHandlerArboriculture extends GuiHandlerBase {

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int cleanId = decodeGuiID(id);

		if (cleanId >= GuiId.values().length)
			return null;

		switch (GuiId.values()[cleanId]) {

			case ArboristChestGUI:
				return this.getNaturalistChestContainer(TreeHelper.UID, player, world, x, y, z, decodeGuiData(id));

			case TreealyzerGUI:
				ItemStack equipped = getEquippedItem(player);
				if (equipped == null)
					return null;

				return new ContainerTreealyzer(player.inventory, new TreealyzerInventory(player, equipped));

			default:
				return null;

		}

	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int cleanId = decodeGuiID(id);
		
		if (cleanId >= GuiId.values().length)
			return null;

		switch (GuiId.values()[cleanId]) {

			case ArboristChestGUI:
				return this.getNaturalistChestGui(TreeHelper.UID, player, world, x, y, z, decodeGuiData(id));

			case TreealyzerGUI:
				ItemStack equipped = getEquippedItem(player);
				if (equipped == null)
					return null;

				return new GuiTreealyzer(player, new TreealyzerInventory(player, equipped));

			default:
				return null;

		}

	}
}
