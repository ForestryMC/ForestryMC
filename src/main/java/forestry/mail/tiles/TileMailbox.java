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
package forestry.mail.tiles;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.authlib.GameProfile;

import forestry.api.mail.ILetter;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.IPostalState;
import forestry.api.mail.PostManager;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.tiles.TileBase;
import forestry.mail.EnumDeliveryState;
import forestry.mail.POBox;
import forestry.mail.PostRegistry;
import forestry.mail.features.MailTiles;
import forestry.mail.gui.ContainerMailbox;

public class TileMailbox extends TileBase {

	public TileMailbox(BlockPos pos, BlockState state) {
		super(MailTiles.MAILBOX.tileType(), pos, state);
		setInternalInventory(new InventoryAdapter(POBox.SLOT_SIZE, "Letters").disableAutomation());
	}

	/* GUI */
	@Override
	public void openGui(ServerPlayer player, BlockPos pos) {
		if (level.isClientSide) {
			return;
		}

		ItemStack heldItem = player.getItemInHand(player.getUsedItemHand());
		// Handle letter sending
		if (PostManager.postRegistry.isLetter(heldItem)) {
			IPostalState result = this.tryDispatchLetter(heldItem);
			if (!result.isOk()) {
				player.sendSystemMessage(result.getDescription());
			} else {
				heldItem.shrink(1);
			}
		} else {
			super.openGui(player, pos);
		}
	}

	/* MAIL HANDLING */
	public Container getOrCreateMailInventory(Level world, GameProfile playerProfile) {
		if (world.isClientSide) {
			return getInternalInventory();
		}

		IMailAddress address = PostManager.postRegistry.getMailAddress(playerProfile);
		return PostRegistry.getOrCreatePOBox((ServerLevel) world, address);
	}

	private IPostalState tryDispatchLetter(ItemStack letterStack) {
		ILetter letter = PostManager.postRegistry.getLetter(letterStack);
		IPostalState result;

		if (letter != null) {
			//this is only called after !world.isRemote has been checked, so I believe the cast is OK
			ServerLevel world = (ServerLevel) this.level;
			result = PostManager.postRegistry.getPostOffice(world).lodgeLetter(world, letterStack, true);
		} else {
			result = EnumDeliveryState.NOT_MAILABLE;
		}

		return result;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ContainerMailbox(windowId, inv, this);
	}
}
