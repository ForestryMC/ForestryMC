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
package forestry.apiculture;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.apiculture.BeeManager;
import forestry.apiculture.gadgets.TileAlvearyHygroregulator;
import forestry.apiculture.gadgets.TileAlvearyPlain;
import forestry.apiculture.gadgets.TileAlvearySieve;
import forestry.apiculture.gadgets.TileAlvearySwarmer;
import forestry.apiculture.gadgets.TileApiary;
import forestry.apiculture.gadgets.TileBeehouse;
import forestry.apiculture.genetics.BeeHelper;
import forestry.apiculture.gui.ContainerAlveary;
import forestry.apiculture.gui.ContainerAlvearyHygroregulator;
import forestry.apiculture.gui.ContainerAlvearySieve;
import forestry.apiculture.gui.ContainerAlvearySwarmer;
import forestry.apiculture.gui.ContainerApiary;
import forestry.apiculture.gui.ContainerHabitatLocator;
import forestry.apiculture.gui.ContainerImprinter;
import forestry.apiculture.gui.GuiAlveary;
import forestry.apiculture.gui.GuiAlvearyHygroregulator;
import forestry.apiculture.gui.GuiAlvearySieve;
import forestry.apiculture.gui.GuiAlvearySwarmer;
import forestry.apiculture.gui.GuiApiary;
import forestry.apiculture.gui.GuiBeealyzer;
import forestry.apiculture.gui.GuiHabitatLocator;
import forestry.apiculture.gui.GuiImprinter;
import forestry.apiculture.items.ItemBeealyzer.BeealyzerInventory;
import forestry.apiculture.items.ItemHabitatLocator.HabitatLocatorInventory;
import forestry.apiculture.items.ItemImprinter.ImprinterInventory;
import forestry.core.GuiHandlerBase;
import forestry.core.gui.ContainerAlyzer;
import forestry.core.network.GuiId;

public class GuiHandlerApiculture extends GuiHandlerBase {

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int cleanId = decodeGuiID(id);
		int guiData = decodeGuiData(id);

		if (cleanId >= GuiId.values().length) {
			return null;
		}

		ItemStack equipped;
		switch (GuiId.values()[cleanId]) {

			case AlvearyGUI:
				return new GuiAlveary(player.inventory, (TileAlvearyPlain) getTileForestry(world, x, y, z));

			case AlvearySieveGUI:
				return new GuiAlvearySieve(player.inventory, (TileAlvearySieve) getTileForestry(world, x, y, z));

			case AlvearySwarmerGUI:
				return new GuiAlvearySwarmer(player.inventory, (TileAlvearySwarmer) getTileForestry(world, x, y, z));

			case ApiaristChestGUI:
				return getNaturalistChestGui(BeeHelper.UID, player, world, x, y, z, guiData);

			case ApiaryGUI:
				return new GuiApiary(player.inventory, (TileApiary) getTileForestry(world, x, y, z));

			case BeealyzerGUI:
				equipped = player.getCurrentEquippedItem();
				if (equipped == null) {
					return null;
				}

				return new GuiBeealyzer(player, new BeealyzerInventory(player, equipped));

			case BeehouseGUI:
				return new GuiApiary(player.inventory, (TileBeehouse) getTileForestry(world, x, y, z));

			case HabitatLocatorGUI:
				equipped = player.getCurrentEquippedItem();
				if (equipped == null) {
					return null;
				}

				return new GuiHabitatLocator(player, new HabitatLocatorInventory(player, equipped));

			case HygroregulatorGUI:
				return new GuiAlvearyHygroregulator(player.inventory, (TileAlvearyHygroregulator) getTileForestry(world, x, y, z));

			case ImprinterGUI:
				equipped = player.getCurrentEquippedItem();
				if (equipped == null) {
					return null;
				}
				return new GuiImprinter(player.inventory, new ImprinterInventory(player, equipped));

			default:
				return null;

		}
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int cleanId = decodeGuiID(id);
		int guiData = decodeGuiData(id);

		if (cleanId >= GuiId.values().length) {
			return null;
		}

		ItemStack equipped;
		switch (GuiId.values()[cleanId]) {

			case AlvearyGUI:
				synchApiaristTracker(world, player);
				return new ContainerAlveary(player.inventory, (TileAlvearyPlain) getTileForestry(world, x, y, z));

			case AlvearySieveGUI:
				return new ContainerAlvearySieve(player.inventory, (TileAlvearySieve) getTileForestry(world, x, y, z));

			case AlvearySwarmerGUI:
				return new ContainerAlvearySwarmer(player.inventory, (TileAlvearySwarmer) getTileForestry(world, x, y, z));

			case ApiaristChestGUI:
				return getNaturalistChestContainer(BeeHelper.UID, player, world, x, y, z, guiData);

			case ApiaryGUI:
				synchApiaristTracker(world, player);
				return new ContainerApiary(player.inventory, (TileApiary) getTileForestry(world, x, y, z), true);

			case BeealyzerGUI:
				equipped = player.getCurrentEquippedItem();
				if (equipped == null) {
					return null;
				}

				synchApiaristTracker(world, player);
				return new ContainerAlyzer(new BeealyzerInventory(player, equipped), player);

			case BeehouseGUI:
				synchApiaristTracker(world, player);
				return new ContainerApiary(player.inventory, (TileBeehouse) getTileForestry(world, x, y, z), false);

			case HabitatLocatorGUI:
				equipped = player.getCurrentEquippedItem();
				if (equipped == null) {
					return null;
				}

				return new ContainerHabitatLocator(player, new HabitatLocatorInventory(player, equipped));

			case HygroregulatorGUI:
				return new ContainerAlvearyHygroregulator(player.inventory, (TileAlvearyHygroregulator) getTileForestry(world, x, y, z));

			case ImprinterGUI:
				synchApiaristTracker(world, player);
				equipped = player.getCurrentEquippedItem();
				if (equipped == null) {
					return null;
				}
				return new ContainerImprinter(player.inventory, new ImprinterInventory(player, equipped));

			default:
				return null;

		}
	}

	private static void synchApiaristTracker(World world, EntityPlayer player) {
		BeeManager.beeRoot.getBreedingTracker(world, player.getGameProfile()).synchToPlayer(player);
	}
}
