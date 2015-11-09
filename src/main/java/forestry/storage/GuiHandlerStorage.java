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
package forestry.storage;

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.apiculture.BeeManager;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.core.GuiHandlerBase;
import forestry.core.config.Constants;
import forestry.core.gui.GuiNaturalistInventory;
import forestry.core.network.GuiId;
import forestry.storage.gui.ContainerBackpack;
import forestry.storage.gui.ContainerNaturalistBackpack;
import forestry.storage.gui.GuiBackpack;
import forestry.storage.gui.GuiBackpackT2;
import forestry.storage.inventory.ItemInventoryBackpackPaged;
import forestry.storage.items.ItemBackpack;

public class GuiHandlerStorage extends GuiHandlerBase {

	@SideOnly(Side.CLIENT)
	@Override
	public Gui getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int cleanId = decodeGuiID(id);
		int guiData = decodeGuiData(id);

		if (cleanId >= GuiId.values().length) {
			return null;
		}

		GuiId guiId = GuiId.values()[cleanId];

		ItemStack equipped;
		switch (guiId) {
			case ApiaristBackpackGUI:
				return getNaturalistGui(BeeManager.beeRoot, player, guiId, guiData);

			case LepidopteristBackpackGUI:
				return getNaturalistGui(ButterflyManager.butterflyRoot, player, guiId, guiData);

			case BackpackGUI:
				equipped = getBackpackItem(player);
				if (equipped == null) {
					return null;
				}
				return new GuiBackpack(new ContainerBackpack(player, ContainerBackpack.Size.DEFAULT, equipped));

			case BackpackT2GUI:
				equipped = getBackpackItem(player);
				if (equipped == null) {
					return null;
				}
				return new GuiBackpackT2(new ContainerBackpack(player, ContainerBackpack.Size.T2, equipped));

			default:
				return null;

		}
	}

	@SideOnly(Side.CLIENT)
	private static Gui getNaturalistGui(ISpeciesRoot root, EntityPlayer player, GuiId guiId, int guiData) {
		ItemStack equipped = getBackpackItem(player);
		if (equipped == null) {
			return null;
		}
		ItemInventoryBackpackPaged inventory = new ItemInventoryBackpackPaged(player, Constants.SLOTS_BACKPACK_APIARIST, equipped, guiId);
		ContainerNaturalistBackpack container = new ContainerNaturalistBackpack(player, inventory, guiData);
		return new GuiNaturalistInventory(root, player, container, inventory, guiData, 5);
	}

	private static ItemStack getBackpackItem(EntityPlayer player) {
		ItemStack equipped = player.getCurrentEquippedItem();
		if (equipped == null) {
			return null;
		}
		if (equipped.getItem() instanceof ItemBackpack) {
			return equipped;
		}
		return null;
	}

	@Override
	public Container getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int cleanId = decodeGuiID(id);
		int guiData = decodeGuiData(id);
		GuiId guiId = GuiId.values()[cleanId];

		if (cleanId >= GuiId.values().length) {
			return null;
		}

		ItemStack equipped;
		switch (guiId) {
			case ApiaristBackpackGUI:
			case LepidopteristBackpackGUI:
				equipped = getBackpackItem(player);
				if (equipped == null) {
					return null;
				}

				ItemInventoryBackpackPaged inventory = new ItemInventoryBackpackPaged(player, Constants.SLOTS_BACKPACK_APIARIST, equipped, guiId);
				return new ContainerNaturalistBackpack(player, inventory, guiData);

			case BackpackGUI:
				equipped = getBackpackItem(player);
				if (equipped == null) {
					return null;
				}

				return new ContainerBackpack(player, ContainerBackpack.Size.DEFAULT, equipped);

			case BackpackT2GUI:
				equipped = getBackpackItem(player);
				if (equipped == null) {
					return null;
				}

				return new ContainerBackpack(player, ContainerBackpack.Size.T2, equipped);

			default:
				return null;
		}
	}
}
