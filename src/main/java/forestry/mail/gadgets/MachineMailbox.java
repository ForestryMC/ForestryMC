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

import buildcraft.api.statements.ITriggerExternal;
import com.mojang.authlib.GameProfile;
import forestry.api.core.ForestryAPI;
import forestry.api.core.ISpecialInventory;
import forestry.api.mail.ILetter;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.IPostalState;
import forestry.api.mail.PostManager;
import forestry.core.config.Config;
import forestry.core.gadgets.TileBase;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.InventoryAdapter;
import forestry.mail.EnumDeliveryState;
import forestry.mail.IMailContainer;
import forestry.mail.POBox;
import forestry.mail.PostRegistry;
import forestry.plugins.PluginMail;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.LinkedList;

public class MachineMailbox extends TileBase implements IMailContainer, ISpecialInventory, ISidedInventory {

	private boolean isLinked = false;

	public MachineMailbox() {
		setHints(Config.hints.get("mailbox"));
	}

	@Override
	public String getInventoryName() {
		return getUnlocalizedName();
	}

	/* GUI */
	@Override
	public void openGui(EntityPlayer player, TileBase tile) {

		if (!Proxies.common.isSimulating(worldObj))
			return;

		ItemStack held = player.getCurrentEquippedItem();

		// Handle letter sending
		if (PostManager.postRegistry.isLetter(held)) {
			IPostalState result = this.tryDispatchLetter(held, true);
			if (!result.isOk())
				player.addChatMessage(new ChatComponentTranslation("for.chat.mail." + result.getIdentifier()));
			else
				held.stackSize--;
		} else
			player.openGui(ForestryAPI.instance, GuiId.MailboxGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	/* UPDATING */
	@Override
	public void updateServerSide() {
		if (!isLinked) {
			getOrCreateMailInventory(worldObj, getOwnerProfile());
			isLinked = true;
		}
	}

	/* MAIL HANDLING */

	public IInventory getOrCreateMailInventory(World world, GameProfile playerProfile) {
		if (!Proxies.common.isSimulating(world))
			return new InventoryAdapter(POBox.SLOT_SIZE, "Letters");

		IMailAddress address = PostManager.postRegistry.getMailAddress(playerProfile);
		return PostRegistry.getOrCreatePOBox(worldObj, address);
	}

	private IPostalState tryDispatchLetter(ItemStack letterstack, boolean dispatchLetter) {
		ILetter letter = PostManager.postRegistry.getLetter(letterstack);
		IPostalState result;

		if (letter != null)
			result = PostManager.postRegistry.getPostOffice(worldObj).lodgeLetter(worldObj, letterstack, dispatchLetter);
		else
			result = EnumDeliveryState.NOT_MAILABLE;

		return result;
	}

	/* IMAILCONTAINER */
	@Override
	public boolean hasMail() {

		IInventory mailInventory = getOrCreateMailInventory(worldObj, getOwnerProfile());
		for (int i = 0; i < mailInventory.getSizeInventory(); i++)
			if (mailInventory.getStackInSlot(i) != null)
				return true;

		return false;
	}

	/* ITRIGGERPROVIDER */
	@Override
	public LinkedList<ITriggerExternal> getCustomTriggers() {
		LinkedList<ITriggerExternal> res = new LinkedList<ITriggerExternal>();
		res.add(PluginMail.triggerHasMail);
		return res;
	}

	/* ISPECIALINVENTORY */
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		if (!PostManager.postRegistry.isLetter(stack))
			return 0;

		IPostalState result = tryDispatchLetter(stack, doAdd);

		if (!result.isOk())
			return 0;
		else
			return 1;
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {

		ItemStack product = null;
		IInventory mailInventory = getOrCreateMailInventory(worldObj, getOwnerProfile());

		for (int i = 0; i < mailInventory.getSizeInventory(); i++) {
			ItemStack slotStack = mailInventory.getStackInSlot(i);
			if (slotStack == null)
				continue;

			product = slotStack;
			if (doRemove)
				mailInventory.setInventorySlotContents(i, null);
			break;
		}

		if (product != null)
			return new ItemStack[] { product };
		else
			return new ItemStack[0];
	}

	@Override
	public int getSizeInventory() {
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return null;
	}

	@Override
	public ItemStack decrStackSize(int var1, int var2) {
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
	}

	@Override
	public int getInventoryStackLimit() {
		return 0;
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return super.isUseableByPlayer(player);
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean hasCustomInventoryName() {
		return super.hasCustomInventoryName();
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemstack) {
		return super.isItemValidForSlot(slotIndex, itemstack);
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return super.canInsertItem(i, itemstack, j);
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return super.canExtractItem(i, itemstack, j);
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return super.getAccessibleSlotsFromSide(side);
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}
}
