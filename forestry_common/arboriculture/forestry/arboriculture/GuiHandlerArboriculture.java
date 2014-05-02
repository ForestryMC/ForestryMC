/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
