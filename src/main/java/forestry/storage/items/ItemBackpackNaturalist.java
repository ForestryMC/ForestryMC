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
package forestry.storage.items;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;
import forestry.core.ItemGroupForestry;
import forestry.core.config.Constants;
import forestry.storage.gui.ContainerNaturalistBackpack;
import forestry.storage.inventory.ItemInventoryBackpackPaged;

public class ItemBackpackNaturalist extends ItemBackpack {
	private final String rootUid;

	public ItemBackpackNaturalist(String rootUid, IBackpackDefinition definition) {
		this(rootUid, definition, ItemGroupForestry.tabForestry);
	}

	public ItemBackpackNaturalist(String rootUid, IBackpackDefinition definition, CreativeModeTab tab) {
		super(definition, EnumBackpackType.NATURALIST, tab);
		this.rootUid = rootUid;
	}
	//TODO gui
	//	@Override
	//	protected void openGui(ServerPlayerEntity playerEntity, ItemStack stack) {
	//		NetworkHooks.openGui(playerEntity, ContainerNaturalistInventory,
	//		});
	//	}
	//	@Override
	//	@OnlyIn(Dist.CLIENT)
	//	public ContainerScreen getGui(PlayerEntity player, ItemStack heldItem, int page) {
	//		ItemInventoryBackpackPaged inventory = new ItemInventoryBackpackPaged(player, Constants.SLOTS_BACKPACK_APIARIST, heldItem, this);
	//		ContainerNaturalistBackpack container = new ContainerNaturalistBackpack(player, inventory, page);
	//		return new GuiNaturalistInventory(speciesRoot, player, container, page, 5);
	//	}

	@Override
	public AbstractContainerMenu getContainer(int windowId, Player player, ItemStack heldItem) {
		ItemInventoryBackpackPaged inventory = new ItemInventoryBackpackPaged(player, Constants.SLOTS_BACKPACK_APIARIST, heldItem, this);
		return new ContainerNaturalistBackpack(windowId, player.getInventory(), inventory, 0);    //TODO init on first page? Or is this server desync?
	}

	//TODO see if this can be deduped. Given we pass in the held item etc.
	public static class ContainerProvider implements MenuProvider {

		private ItemStack heldItem;

		public ContainerProvider(ItemStack heldItem) {
			this.heldItem = heldItem;
		}

		@Override
		public Component getDisplayName() {
			return Component.literal("ITEM_GUI_TITLE");    //TODO needs to be overriden individually
		}

		@Nullable
		@Override
		public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
			Item item = heldItem.getItem();
			if (!(item instanceof ItemBackpackNaturalist backpack)) {
				return null;
			}
			return backpack.getContainer(windowId, playerEntity, heldItem);
		}
	}
}
