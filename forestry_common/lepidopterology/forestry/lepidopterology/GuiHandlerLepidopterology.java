/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.lepidopterology;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.core.GuiHandlerBase;
import forestry.core.network.GuiId;
import forestry.lepidopterology.genetics.ButterflyHelper;
import forestry.lepidopterology.gui.ContainerFlutterlyzer;
import forestry.lepidopterology.gui.GuiFlutterlyzer;
import forestry.lepidopterology.items.ItemFlutterlyzer.FlutterlyzerInventory;

public class GuiHandlerLepidopterology extends GuiHandlerBase {

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int cleanId = decodeGuiID(id);
		int guiData = decodeGuiData(id);

		if (cleanId >= GuiId.values().length)
			return null;

		switch (GuiId.values()[cleanId]) {
			case FlutterlyzerGUI:
				ItemStack equipped = getEquippedItem(player);
				if (equipped == null)
					return null;

				return new ContainerFlutterlyzer(player.inventory, new FlutterlyzerInventory(player, equipped));

			case LepidopteristChestGUI:
				return getNaturalistChestContainer(ButterflyHelper.UID, player, world, x, y, z, guiData);

			default:
				return null;

		}
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int cleanId = decodeGuiID(id);
		int guiData = decodeGuiData(id);

		if (cleanId >= GuiId.values().length)
			return null;

		switch (GuiId.values()[cleanId]) {
			case FlutterlyzerGUI:
				ItemStack equipped = getEquippedItem(player);
				if (equipped == null)
					return null;

				return new GuiFlutterlyzer(player, new FlutterlyzerInventory(player, equipped));

			case LepidopteristChestGUI:
				return getNaturalistChestGui(ButterflyHelper.UID, player, world, x, y, z, guiData);

			default:
				return null;

		}
	}
}
