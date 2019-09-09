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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import com.mojang.authlib.GameProfile;

import forestry.api.mail.ILetter;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.IPostalState;
import forestry.api.mail.PostManager;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.tiles.TileBase;
import forestry.mail.EnumDeliveryState;
import forestry.mail.ModuleMail;
import forestry.mail.POBox;
import forestry.mail.PostRegistry;
import forestry.mail.gui.ContainerMailbox;

public class TileMailbox extends TileBase {

	public TileMailbox() {
		super(ModuleMail.getTiles().MAILBOX);
		setInternalInventory(new InventoryAdapter(POBox.SLOT_SIZE, "Letters").disableAutomation());
	}

	/* GUI */
	@Override
	public void openGui(ServerPlayerEntity player, BlockPos pos) {
		if (world.isRemote) {
			return;
		}

		ItemStack heldItem = player.getHeldItem(player.getActiveHand());
		// Handle letter sending
		if (PostManager.postRegistry.isLetter(heldItem)) {
			IPostalState result = this.tryDispatchLetter(heldItem);
			if (!result.isOk()) {
				player.sendMessage(new StringTextComponent(result.getDescription()));
			} else {
				heldItem.shrink(1);
			}
		} else {
			super.openGui(player, pos);
		}
	}

	/* MAIL HANDLING */
	public IInventory getOrCreateMailInventory(World world, GameProfile playerProfile) {
		if (world.isRemote) {
			return getInternalInventory();
		}

		IMailAddress address = PostManager.postRegistry.getMailAddress(playerProfile);
		return PostRegistry.getOrCreatePOBox((ServerWorld) world, address);
	}

	private IPostalState tryDispatchLetter(ItemStack letterStack) {
		ILetter letter = PostManager.postRegistry.getLetter(letterStack);
		IPostalState result;

		if (letter != null) {
			//this is only called after !world.isRemote has been checked, so I believe the cast is OK
			ServerWorld world = (ServerWorld) this.world;
			result = PostManager.postRegistry.getPostOffice(world).lodgeLetter(world, letterStack, true);
		} else {
			result = EnumDeliveryState.NOT_MAILABLE;
		}

		return result;
	}

	//	@Optional.Method(modid = Constants.BCLIB_MOD_ID)
	//	@Override
	//	public void addExternalTriggers(Collection<ITriggerExternal> triggers, @Nonnull Direction side, TileEntity tile) {
	//		super.addExternalTriggers(triggers, side, tile);
	//		// triggers.add(MailTriggers.triggerHasMail);
	//	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
		return new ContainerMailbox(windowId, inv, this);
	}
}
