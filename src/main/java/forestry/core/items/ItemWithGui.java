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

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import net.minecraftforge.network.NetworkHooks;

import forestry.core.gui.ContainerItemInventory;
import forestry.core.network.PacketBufferForestry;

public abstract class ItemWithGui extends ItemForestry {

	public ItemWithGui(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);

		if (!worldIn.isClientSide) {
			ServerPlayer sPlayer = (ServerPlayer) playerIn;    //TODO safe?
			openGui(sPlayer, stack);
		}

		return InteractionResultHolder.success(stack);
	}

	protected void openGui(ServerPlayer player, ItemStack stack) {
		NetworkHooks.openScreen(player, new ContainerProvider(stack), buffer -> writeContainerData(player, stack, new PacketBufferForestry(buffer)));
	}

	protected void writeContainerData(ServerPlayer player, ItemStack stack, PacketBufferForestry buffer) {
		buffer.writeBoolean(player.getUsedItemHand() == InteractionHand.MAIN_HAND);
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack itemstack, Player player) {
		if (!itemstack.isEmpty() &&
				player instanceof ServerPlayer &&
				player.containerMenu instanceof ContainerItemInventory) {
			player.closeContainer();
		}

		return super.onDroppedByPlayer(itemstack, player);
	}

	@Nullable
	public abstract AbstractContainerMenu getContainer(int windowId, Player player, ItemStack heldItem);

	public static class ContainerProvider implements MenuProvider {

		private final ItemStack heldItem;

		public ContainerProvider(ItemStack heldItem) {
			this.heldItem = heldItem;
		}

		@Override
		public Component getDisplayName() {
			return heldItem.getHoverName();
		}

		@Nullable
		@Override
		public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
			Item item = heldItem.getItem();
			if (!(item instanceof ItemWithGui itemWithGui)) {
				return null;
			}
			return itemWithGui.getContainer(windowId, playerEntity, heldItem);
		}
	}

}
