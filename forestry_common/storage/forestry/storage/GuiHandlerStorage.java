/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.storage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.GuiHandlerBase;
import forestry.core.config.Defaults;
import forestry.core.gui.GuiNaturalistInventory;
import forestry.core.gui.IPagedInventory;
import forestry.core.network.GuiId;
import forestry.core.utils.ItemInventory;
import forestry.storage.gui.ContainerBackpack;
import forestry.storage.gui.ContainerNaturalistBackpack;
import forestry.storage.gui.GuiBackpack;
import forestry.storage.gui.GuiBackpackT2;
import forestry.storage.items.ItemBackpack;

public class GuiHandlerStorage extends GuiHandlerBase {

	public static class PagedInventory extends ItemInventory implements IPagedInventory {

		//private final int x, y, z;
		private final int guiId;

		public PagedInventory(Class<? extends Item> itemClass, int size, ItemStack itemstack, int x, int y, int z, int guiId) {
			super(itemClass, size, itemstack);
			/*this.x = x;
			this.y = y;
			this.z = z;*/
			this.guiId = guiId;
		}

		@Override
		public void flipPage(EntityPlayer player, int page) {
			onGuiSaved(player);
			player.openGui(ForestryAPI.instance, encodeGuiData(guiId, page), player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
		}
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int cleanId = decodeGuiID(id);
		int guiData = decodeGuiData(id);

		if (cleanId >= GuiId.values().length)
			return null;

		ItemStack equipped;
		switch (GuiId.values()[cleanId]) {

		case ApiaristBackpackGUI:
			equipped = getBackpackItem(player);
			if (equipped == null)
				return null;
			ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot("rootBees");
			PagedInventory inventory = new PagedInventory(ItemBackpack.class, Defaults.SLOTS_BACKPACK_APIARIST, equipped, x, y, z, cleanId);
			return new GuiNaturalistInventory(speciesRoot, player, new ContainerNaturalistBackpack(speciesRoot, player.inventory, inventory, guiData, 25), inventory, guiData, 5);

		case BackpackGUI:
			equipped = getBackpackItem(player);
			if (equipped == null)
				return null;
			return new GuiBackpack(new ContainerBackpack(player, new ItemInventory(ItemBackpack.class, Defaults.SLOTS_BACKPACK_DEFAULT, equipped)));

		case BackpackT2GUI:
			equipped = getBackpackItem(player);
			if (equipped == null)
				return null;
			return new GuiBackpackT2(new ContainerBackpack(player, new ItemInventory(ItemBackpack.class, Defaults.SLOTS_BACKPACK_T2, equipped)));

		case LepidopteristBackpackGUI:
			equipped = getBackpackItem(player);
			if (equipped == null)
				return null;
			speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot("rootButterflies");
			inventory = new PagedInventory(ItemBackpack.class, Defaults.SLOTS_BACKPACK_APIARIST, equipped, x, y, z, id);
			return new GuiNaturalistInventory(speciesRoot, player, new ContainerNaturalistBackpack(speciesRoot, player.inventory, inventory, guiData, 25), inventory, guiData, 5);

		default:
			return null;

		}
	}

	private ItemStack getBackpackItem(EntityPlayer player) {
		ItemStack equipped = getEquippedItem(player);
		if (equipped == null)
			return null;
		if (equipped.getItem() instanceof ItemBackpack)
			return equipped;
		return null;
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int cleanId = decodeGuiID(id);
		int guiData = decodeGuiData(id);

		if (cleanId >= GuiId.values().length)
			return null;

		ItemStack equipped;
		switch (GuiId.values()[cleanId]) {

		case ApiaristBackpackGUI:
			equipped = getBackpackItem(player);
			if (equipped == null)
				return null;

			ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot("rootBees");
			speciesRoot.getBreedingTracker(world, player.getGameProfile()).synchToPlayer(player);
			return new ContainerNaturalistBackpack(speciesRoot, player.inventory, new PagedInventory(ItemBackpack.class, Defaults.SLOTS_BACKPACK_APIARIST, equipped, x, y, z, cleanId), guiData, 25);

		case BackpackGUI:
			equipped = getBackpackItem(player);
			if (equipped == null)
				return null;

			return new ContainerBackpack(player, new ItemInventory(ItemBackpack.class, Defaults.SLOTS_BACKPACK_DEFAULT, equipped));

		case BackpackT2GUI:
			equipped = getBackpackItem(player);
			if (equipped == null)
				return null;

			return new ContainerBackpack(player, new ItemInventory(ItemBackpack.class, Defaults.SLOTS_BACKPACK_T2, equipped));

		case LepidopteristBackpackGUI:
			equipped = getBackpackItem(player);
			if (equipped == null)
				return null;

			speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot("rootButterflies");
			speciesRoot.getBreedingTracker(world, player.getGameProfile()).synchToPlayer(player);
			return new ContainerNaturalistBackpack(speciesRoot, player.inventory, new PagedInventory(ItemBackpack.class, Defaults.SLOTS_BACKPACK_APIARIST, equipped, x, y, z, cleanId), guiData, 25);

		default:
			return null;

		}
	}
}
