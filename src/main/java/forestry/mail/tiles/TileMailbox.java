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

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

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
import forestry.mail.gui.ContainerMailbox;
import forestry.mail.gui.GuiMailbox;

public class TileMailbox extends TileBase {

	public TileMailbox() {
		super("mailbox");
		setInternalInventory(new InventoryAdapter(POBox.SLOT_SIZE, "Letters").disableAutomation());
	}

	/* GUI */
	@Override
	public void openGui(EntityPlayer player, ItemStack heldItem) {
		if (worldObj.isRemote) {
			return;
		}

		// Handle letter sending
		if (PostManager.postRegistry.isLetter(heldItem)) {
			IPostalState result = this.tryDispatchLetter(heldItem);
			if (!result.isOk()) {
				player.addChatMessage(new TextComponentString(result.getDescription()));
			} else {
				heldItem.stackSize--;
			}
		} else {
			super.openGui(player, heldItem);
		}
	}

	/* MAIL HANDLING */
	public IInventory getOrCreateMailInventory(World world, @Nonnull GameProfile playerProfile) {
		if (world.isRemote) {
			return getInternalInventory();
		}

		IMailAddress address = PostManager.postRegistry.getMailAddress(playerProfile);
		return PostRegistry.getOrCreatePOBox(worldObj, address);
	}

	private IPostalState tryDispatchLetter(ItemStack letterStack) {
		ILetter letter = PostManager.postRegistry.getLetter(letterStack);
		IPostalState result;

		if (letter != null) {
			result = PostManager.postRegistry.getPostOffice(worldObj).lodgeLetter(worldObj, letterStack, true);
		} else {
			result = EnumDeliveryState.NOT_MAILABLE;
		}

		return result;
	}

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return new GuiMailbox(player.inventory, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerMailbox(player.inventory, this);
	}
}
