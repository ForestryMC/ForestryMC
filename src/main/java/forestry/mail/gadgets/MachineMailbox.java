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
package forestry.mail.gadgets;

import java.util.Collection;
import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.Optional;

import forestry.api.core.ForestryAPI;
import forestry.api.mail.ILetter;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.IPostalState;
import forestry.api.mail.PostManager;
import forestry.core.config.Config;
import forestry.core.gadgets.TileBase;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.mail.EnumDeliveryState;
import forestry.mail.IMailContainer;
import forestry.mail.POBox;
import forestry.mail.PostRegistry;
import forestry.mail.triggers.MailTriggers;

import buildcraft.api.statements.ITriggerExternal;

public class MachineMailbox extends TileBase implements IMailContainer {

	private boolean isLinked = false;

	public MachineMailbox() {
		setHints(Config.hints.get("mailbox"));
		setInternalInventory(new InventoryAdapter(POBox.SLOT_SIZE, "Letters").disableAutomation());
	}

	/* GUI */
	@Override
	public void openGui(EntityPlayer player, TileBase tile) {

		if (!Proxies.common.isSimulating(worldObj)) {
			return;
		}

		ItemStack held = player.getCurrentEquippedItem();

		// Handle letter sending
		if (PostManager.postRegistry.isLetter(held)) {
			IPostalState result = this.tryDispatchLetter(held, true);
			if (!result.isOk()) {
				player.addChatMessage(new ChatComponentTranslation("for.chat.mail." + result.getIdentifier()));
			} else {
				held.stackSize--;
			}
		} else {
			player.openGui(ForestryAPI.instance, GuiId.MailboxGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
		}
	}

	/* UPDATING */
	@Override
	public void updateServerSide() {
		if (!isLinked) {
			getOrCreateMailInventory(worldObj, getAccessHandler().getOwner());
			isLinked = true;
		}
	}

	/* MAIL HANDLING */
	public IInventory getOrCreateMailInventory(World world, GameProfile playerProfile) {
		if (!Proxies.common.isSimulating(world)) {
			return getInternalInventory();
		}

		IMailAddress address = PostManager.postRegistry.getMailAddress(playerProfile);
		return PostRegistry.getOrCreatePOBox(worldObj, address);
	}

	private IPostalState tryDispatchLetter(ItemStack letterstack, boolean dispatchLetter) {
		ILetter letter = PostManager.postRegistry.getLetter(letterstack);
		IPostalState result;

		if (letter != null) {
			result = PostManager.postRegistry.getPostOffice(worldObj).lodgeLetter(worldObj, letterstack, dispatchLetter);
		} else {
			result = EnumDeliveryState.NOT_MAILABLE;
		}

		return result;
	}

	/* IMAILCONTAINER */
	@Override
	public boolean hasMail() {

		IInventory mailInventory = getOrCreateMailInventory(worldObj, getAccessHandler().getOwner());
		for (int i = 0; i < mailInventory.getSizeInventory(); i++) {
			if (mailInventory.getStackInSlot(i) != null) {
				return true;
			}
		}

		return false;
	}

	/* ITRIGGERPROVIDER */
	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
		LinkedList<ITriggerExternal> res = new LinkedList<ITriggerExternal>();
		res.add(MailTriggers.triggerHasMail);
		return res;
	}
}
