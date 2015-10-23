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
package forestry.lepidopterology;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.lepidopterology.ButterflyManager;
import forestry.core.GuiHandlerBase;
import forestry.core.gui.ContainerAlyzer;
import forestry.core.network.GuiId;
import forestry.lepidopterology.gui.GuiFlutterlyzer;
import forestry.lepidopterology.items.ItemFlutterlyzer.FlutterlyzerInventory;

public class GuiHandlerLepidopterology extends GuiHandlerBase {

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int cleanId = decodeGuiID(id);
		int guiData = decodeGuiData(id);

		if (cleanId >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[cleanId]) {
			case FlutterlyzerGUI:
				ItemStack equipped = player.getCurrentEquippedItem();
				if (equipped == null) {
					return null;
				}

				return new ContainerAlyzer(new FlutterlyzerInventory(player, equipped), player);

			case LepidopteristChestGUI:
				return getNaturalistChestContainer(ButterflyManager.butterflyRoot, player, world, x, y, z, guiData);

			default:
				return null;

		}
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int cleanId = decodeGuiID(id);
		int guiData = decodeGuiData(id);

		if (cleanId >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[cleanId]) {
			case FlutterlyzerGUI:
				ItemStack equipped = player.getCurrentEquippedItem();
				if (equipped == null) {
					return null;
				}

				return new GuiFlutterlyzer(player, new FlutterlyzerInventory(player, equipped));

			case LepidopteristChestGUI:
				return getNaturalistChestGui(ButterflyManager.butterflyRoot, player, world, x, y, z, guiData);

			default:
				return null;

		}
	}
}
