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
package forestry.apiculture.items;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import net.minecraftforge.fml.network.NetworkHooks;

import forestry.api.core.ItemGroups;
import forestry.apiculture.gui.ContainerImprinter;
import forestry.apiculture.inventory.ItemInventoryImprinter;
import forestry.core.items.ItemWithGui;

public class ItemImprinter extends ItemWithGui {
	public ItemImprinter() {
		super((new Item.Properties()).group(ItemGroups.tabApiculture).maxStackSize(1));
	}

	@Override
	public Container getContainer(int windowId, PlayerEntity player, ItemStack heldItem) {
		return new ContainerImprinter(windowId, player.inventory, new ItemInventoryImprinter(player, heldItem));
	}

	@Override
	public void openGui(ServerPlayerEntity player, ItemStack stack) {
		NetworkHooks.openGui(player, new ContainerProvider(stack), p -> p.writeBoolean(player.getActiveHand() == Hand.MAIN_HAND));
	}

	//TODO see if this can be deduped. Given we pass in the held item etc.
	public static class ContainerProvider implements INamedContainerProvider {

		private ItemStack heldItem;

		public ContainerProvider(ItemStack heldItem) {
			this.heldItem = heldItem;
		}

		@Override
		public ITextComponent getDisplayName() {
			return new StringTextComponent("ITEM_GUI_TITLE");    //TODO needs to be overriden individually
		}

		@Nullable
		@Override
		public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
			return new ContainerImprinter(windowId, playerInventory, new ItemInventoryImprinter(playerEntity, heldItem));
		}
	}
}
