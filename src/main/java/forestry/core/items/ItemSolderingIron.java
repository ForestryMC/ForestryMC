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
package forestry.core.items;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import net.minecraftforge.fml.network.NetworkHooks;

import forestry.core.ItemGroupForestry;
import forestry.core.circuits.ContainerSolderingIron;
import forestry.core.circuits.ISolderingIron;
import forestry.core.inventory.ItemInventorySolderingIron;

public class ItemSolderingIron extends ItemWithGui implements ISolderingIron {

	public ItemSolderingIron(Properties properties) {
		super(properties.group(ItemGroupForestry.tabForestry));
	}

	@Override
	public Container getContainer(int windowId, PlayerEntity player, ItemStack heldItem) {
		return new ContainerSolderingIron(windowId, player, new ItemInventorySolderingIron(player, heldItem));
	}

	//TODO see about deduping this too.
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
			return new ContainerSolderingIron(windowId, playerEntity, new ItemInventorySolderingIron(playerEntity, heldItem));
		}
	}
}
