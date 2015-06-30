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
package forestry.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;

import forestry.core.circuits.ContainerSolderingIron;
import forestry.core.circuits.GuiSolderingIron;
import forestry.core.circuits.ItemSolderingIron.SolderingInventory;
import forestry.core.gadgets.TileAnalyzer;
import forestry.core.gadgets.TileEscritoire;
import forestry.core.gui.ContainerAnalyzer;
import forestry.core.gui.ContainerEscritoire;
import forestry.core.gui.GuiAnalyzer;
import forestry.core.gui.GuiEscritoire;
import forestry.core.network.GuiId;
import forestry.plugins.PluginManager;

public class GuiHandler extends GuiHandlerBase {

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int cleanId = decodeGuiID(id);

		if (cleanId < GuiId.values().length) {
			switch (GuiId.values()[cleanId]) {

				case AnalyzerGUI:
					return new GuiAnalyzer(player.inventory, getTile(world, x, y, z, player, TileAnalyzer.class));

				case NaturalistBenchGUI:
					return new GuiEscritoire(player, getTile(world, x, y, z, player, TileEscritoire.class));

				case SolderingIronGUI:
					ItemStack equipped = player.getCurrentEquippedItem();
					if (equipped == null) {
						return null;
					}
					return new GuiSolderingIron(player, new SolderingInventory(player, equipped));

				default:
					for (IGuiHandler handler : PluginManager.guiHandlers) {
						Object element = handler.getClientGuiElement(id, player, world, x, y, z);
						if (element != null) {
							return element;
						}
					}

					return null;
			}
		}

		return null;
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int cleanId = decodeGuiID(id);

		if (cleanId < GuiId.values().length) {
			switch (GuiId.values()[cleanId]) {

				case AnalyzerGUI:
					return new ContainerAnalyzer(player.inventory, getTile(world, x, y, z, player, TileAnalyzer.class));

				case NaturalistBenchGUI:
					return new ContainerEscritoire(player, getTile(world, x, y, z, player, TileEscritoire.class));

				case SolderingIronGUI:
					ItemStack equipped = player.getCurrentEquippedItem();
					if (equipped == null) {
						return null;
					}
					return new ContainerSolderingIron(player, new SolderingInventory(player, equipped));

				default:
					for (IGuiHandler handler : PluginManager.guiHandlers) {
						Object element = handler.getServerGuiElement(id, player, world, x, y, z);
						if (element != null) {
							return element;
						}
					}

					return null;

			}
		}

		return null;
	}
}
