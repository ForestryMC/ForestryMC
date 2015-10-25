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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.GuiHandlerBase;
import forestry.core.config.Defaults;
import forestry.core.gui.GuiNaturalistInventory;
import forestry.core.gui.IPagedInventory;
import forestry.core.inventory.ItemInventoryBackpack;
import forestry.core.network.GuiId;
import forestry.storage.gui.ContainerBackpack;
import forestry.storage.gui.ContainerNaturalistBackpack;
import forestry.storage.gui.GuiBackpack;
import forestry.storage.gui.GuiBackpackT2;
import forestry.storage.items.ItemBackpack;

public class GuiHandlerStorage extends GuiHandlerBase {

	public static class PagedBackpackInventory extends ItemInventoryBackpack implements IPagedInventory {

		private final int guiId;

		public PagedBackpackInventory(EntityPlayer player, int size, ItemStack itemstack, int guiId) {
			super(player, size, itemstack);
			this.guiId = guiId;
		}

		@Override
		public void flipPage(EntityPlayer player, int page) {
			player.openGui(ForestryAPI.instance, encodeGuiData(guiId, page), player.worldObj, (int) player.posX,
					(int) player.posY, (int) player.posZ);
		}
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int cleanId = decodeGuiID(id);
		int guiData = decodeGuiData(id);

		if (cleanId >= GuiId.values().length) {
			return null;
		}

		ItemStack equipped;
		switch (GuiId.values()[cleanId]) {

		case ApiaristBackpackGUI:
			equipped = getBackpackItem(player);
			if (equipped == null) {
				return null;
			}
			ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot("rootBees");
			PagedBackpackInventory inventory = new PagedBackpackInventory(player, Defaults.SLOTS_BACKPACK_APIARIST,
					equipped, cleanId);
			return new GuiNaturalistInventory(speciesRoot, player,
					new ContainerNaturalistBackpack(player, inventory, guiData), inventory, guiData, 5);

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

		case LepidopteristBackpackGUI:
			equipped = getBackpackItem(player);
			if (equipped == null) {
				return null;
			}
			speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot("rootButterflies");
			inventory = new PagedBackpackInventory(player, Defaults.SLOTS_BACKPACK_APIARIST, equipped, id);
			return new GuiNaturalistInventory(speciesRoot, player,
					new ContainerNaturalistBackpack(player, inventory, guiData), inventory, guiData, 5);

		default:
			return null;

		}
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
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int cleanId = decodeGuiID(id);
		int guiData = decodeGuiData(id);

		if (cleanId >= GuiId.values().length) {
			return null;
		}

		ItemStack equipped;
		switch (GuiId.values()[cleanId]) {

		case ApiaristBackpackGUI:
			equipped = getBackpackItem(player);
			if (equipped == null) {
				return null;
			}

			return new ContainerNaturalistBackpack(player,
					new PagedBackpackInventory(player, Defaults.SLOTS_BACKPACK_APIARIST, equipped, cleanId), guiData);

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

		case LepidopteristBackpackGUI:
			equipped = getBackpackItem(player);
			if (equipped == null) {
				return null;
			}

			return new ContainerNaturalistBackpack(player,
					new PagedBackpackInventory(player, Defaults.SLOTS_BACKPACK_APIARIST, equipped, cleanId), guiData);

		default:
			return null;

		}
	}
}
