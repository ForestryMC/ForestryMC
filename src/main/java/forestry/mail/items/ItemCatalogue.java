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
package forestry.mail.items;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import net.minecraftforge.fml.network.NetworkHooks;

import forestry.core.ItemGroupForestry;
import forestry.core.items.ItemWithGui;
import forestry.mail.gui.ContainerCatalogue;

public class ItemCatalogue extends ItemWithGui {

	public ItemCatalogue() {
		super((new Item.Properties()).group(ItemGroupForestry.tabForestry));
	}

	@Override
	protected void openGui(ServerPlayerEntity player, ItemStack stack) {
		NetworkHooks.openGui(player, new ContainerProvider());
	}

	//TODO see if this can be deduped. Given we pass in the held item etc.
	public static class ContainerProvider implements INamedContainerProvider {

		public ContainerProvider() {
		}

		@Override
		public ITextComponent getDisplayName() {
			return new StringTextComponent("ITEM_GUI_TITLE");    //TODO needs to be overriden individually
		}

		@Nullable
		@Override
		public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
			return new ContainerCatalogue(windowId, playerInventory);
		}
	}

	@Nullable
	@Override
	public Container getContainer(int windowId, PlayerEntity player, ItemStack heldItem) {
		return new ContainerCatalogue(windowId, player.inventory);
	}
}
