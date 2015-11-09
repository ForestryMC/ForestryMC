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

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.apiculture.BeeManager;
import forestry.apiculture.entities.EntityMinecartApiary;
import forestry.apiculture.entities.EntityMinecartBeehouse;
import forestry.apiculture.gui.ContainerAlveary;
import forestry.apiculture.gui.ContainerAlvearyHygroregulator;
import forestry.apiculture.gui.ContainerAlvearySieve;
import forestry.apiculture.gui.ContainerAlvearySwarmer;
import forestry.apiculture.gui.ContainerBeeHousing;
import forestry.apiculture.gui.ContainerHabitatLocator;
import forestry.apiculture.gui.ContainerImprinter;
import forestry.apiculture.gui.ContainerMinecartBeehouse;
import forestry.apiculture.gui.GuiAlveary;
import forestry.apiculture.gui.GuiAlvearyHygroregulator;
import forestry.apiculture.gui.GuiAlvearySieve;
import forestry.apiculture.gui.GuiAlvearySwarmer;
import forestry.apiculture.gui.GuiBeeHousing;
import forestry.apiculture.gui.GuiBeealyzer;
import forestry.apiculture.gui.GuiHabitatLocator;
import forestry.apiculture.gui.GuiImprinter;
import forestry.apiculture.inventory.ItemInventoryBeealyzer;
import forestry.apiculture.inventory.ItemInventoryHabitatLocator;
import forestry.apiculture.inventory.ItemInventoryImprinter;
import forestry.apiculture.multiblock.TileAlvearyHygroregulator;
import forestry.apiculture.multiblock.TileAlvearyPlain;
import forestry.apiculture.multiblock.TileAlvearySieve;
import forestry.apiculture.multiblock.TileAlvearySwarmer;
import forestry.apiculture.tiles.TileApiary;
import forestry.apiculture.tiles.TileBeehouse;
import forestry.core.GuiHandlerBase;
import forestry.core.gui.ContainerAlyzer;
import forestry.core.network.GuiId;
import forestry.core.tiles.TileUtil;

public class GuiHandlerApiculture extends GuiHandlerBase {

	@SideOnly(Side.CLIENT)
	@Override
	public Gui getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int cleanId = decodeGuiID(id);
		int guiData = decodeGuiData(id);

		if (cleanId >= GuiId.values().length) {
			return null;
		}

		ItemStack equipped;
		switch (GuiId.values()[cleanId]) {

			case AlvearyGUI:
				return new GuiAlveary(player.inventory, TileUtil.getTile(world, x, y, z, TileAlvearyPlain.class));

			case AlvearySieveGUI:
				return new GuiAlvearySieve(player.inventory, TileUtil.getTile(world, x, y, z, TileAlvearySieve.class));

			case AlvearySwarmerGUI:
				return new GuiAlvearySwarmer(player.inventory, TileUtil.getTile(world, x, y, z, TileAlvearySwarmer.class));

			case ApiaristChestGUI:
				return getNaturalistChestGui(BeeManager.beeRoot, player, world, x, y, z, guiData);

			case ApiaryGUI: {
				TileApiary tileApiary = TileUtil.getTile(world, x, y, z, TileApiary.class);
				ContainerBeeHousing container = new ContainerBeeHousing(player.inventory, tileApiary, true);
				return new GuiBeeHousing<>(tileApiary, container, GuiBeeHousing.Icon.APIARY);
			}

			case BeealyzerGUI:
				equipped = player.getCurrentEquippedItem();
				if (equipped == null) {
					return null;
				}

				return new GuiBeealyzer(player, new ItemInventoryBeealyzer(player, equipped));

			case BeehouseGUI: {
				TileBeehouse tileBeehouse = TileUtil.getTile(world, x, y, z, TileBeehouse.class);
				ContainerBeeHousing container = new ContainerBeeHousing(player.inventory, tileBeehouse, false);
				return new GuiBeeHousing<>(tileBeehouse, container, GuiBeeHousing.Icon.BEE_HOUSE);
			}

			case MinecartBeehouseGUI: {
				Entity entity = world.getEntityByID(x);
				if (!(entity instanceof EntityMinecartBeehouse)) {
					return null;
				}

				EntityMinecartBeehouse beeCart = (EntityMinecartBeehouse) entity;
				ContainerMinecartBeehouse container = new ContainerMinecartBeehouse(player.inventory, beeCart, false);
				return new GuiBeeHousing<>(beeCart, container, GuiBeeHousing.Icon.BEE_HOUSE);
			}

			case MinecartApiaryGUI: {
				Entity entity = world.getEntityByID(x);
				if (!(entity instanceof EntityMinecartApiary)) {
					return null;
				}

				EntityMinecartApiary beeCart = (EntityMinecartApiary) entity;
				ContainerMinecartBeehouse container = new ContainerMinecartBeehouse(player.inventory, beeCart, true);
				return new GuiBeeHousing<>(beeCart, container, GuiBeeHousing.Icon.APIARY);
			}

			case HabitatLocatorGUI:
				equipped = player.getCurrentEquippedItem();
				if (equipped == null) {
					return null;
				}

				return new GuiHabitatLocator(player, new ItemInventoryHabitatLocator(player, equipped));

			case HygroregulatorGUI:
				return new GuiAlvearyHygroregulator(player.inventory, TileUtil.getTile(world, x, y, z, TileAlvearyHygroregulator.class));

			case ImprinterGUI:
				equipped = player.getCurrentEquippedItem();
				if (equipped == null) {
					return null;
				}
				return new GuiImprinter(player.inventory, new ItemInventoryImprinter(player, equipped));

			default:
				return null;

		}
	}

	@Override
	public Container getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int cleanId = decodeGuiID(id);
		int guiData = decodeGuiData(id);

		if (cleanId >= GuiId.values().length) {
			return null;
		}

		ItemStack equipped;
		switch (GuiId.values()[cleanId]) {

			case AlvearyGUI:
				synchApiaristTracker(world, player);
				return new ContainerAlveary(player.inventory, TileUtil.getTile(world, x, y, z, TileAlvearyPlain.class));

			case AlvearySieveGUI:
				return new ContainerAlvearySieve(player.inventory, TileUtil.getTile(world, x, y, z, TileAlvearySieve.class));

			case AlvearySwarmerGUI:
				return new ContainerAlvearySwarmer(player.inventory, TileUtil.getTile(world, x, y, z, TileAlvearySwarmer.class));

			case ApiaristChestGUI:
				return getNaturalistChestContainer(BeeManager.beeRoot, player, world, x, y, z, guiData);

			case ApiaryGUI:
				synchApiaristTracker(world, player);
				return new ContainerBeeHousing(player.inventory, TileUtil.getTile(world, x, y, z, TileApiary.class), true);

			case BeealyzerGUI:
				equipped = player.getCurrentEquippedItem();
				if (equipped == null) {
					return null;
				}

				synchApiaristTracker(world, player);
				return new ContainerAlyzer(new ItemInventoryBeealyzer(player, equipped), player);

			case BeehouseGUI:
				synchApiaristTracker(world, player);
				return new ContainerBeeHousing(player.inventory, TileUtil.getTile(world, x, y, z, TileBeehouse.class), false);

			case MinecartBeehouseGUI: {
				Entity entity = world.getEntityByID(x);
				if (!(entity instanceof EntityMinecartBeehouse)) {
					return null;
				}

				synchApiaristTracker(world, player);
				return new ContainerMinecartBeehouse(player.inventory, (EntityMinecartBeehouse) entity, false);
			}

			case MinecartApiaryGUI: {
				Entity entity = world.getEntityByID(x);
				if (!(entity instanceof EntityMinecartApiary)) {
					return null;
				}

				synchApiaristTracker(world, player);
				return new ContainerMinecartBeehouse(player.inventory, (EntityMinecartApiary) entity, true);
			}

			case HabitatLocatorGUI:
				equipped = player.getCurrentEquippedItem();
				if (equipped == null) {
					return null;
				}

				return new ContainerHabitatLocator(player, new ItemInventoryHabitatLocator(player, equipped));

			case HygroregulatorGUI:
				return new ContainerAlvearyHygroregulator(player.inventory, TileUtil.getTile(world, x, y, z, TileAlvearyHygroregulator.class));

			case ImprinterGUI:
				synchApiaristTracker(world, player);
				equipped = player.getCurrentEquippedItem();
				if (equipped == null) {
					return null;
				}
				return new ContainerImprinter(player.inventory, new ItemInventoryImprinter(player, equipped));

			default:
				return null;

		}
	}

	private static void synchApiaristTracker(World world, EntityPlayer player) {
		BeeManager.beeRoot.getBreedingTracker(world, player.getGameProfile()).synchToPlayer(player);
	}
}
