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

import com.google.common.collect.ImmutableSet;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorState;
import forestry.api.mail.ILetter;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.ForestryItem;
import forestry.core.interfaces.IErrorSource;
import forestry.core.interfaces.IHintSource;
import forestry.core.inventory.ItemInventory;
import forestry.core.items.ItemInventoried;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
import forestry.core.utils.GuiUtil;
import forestry.core.utils.StringUtil;
import forestry.mail.Letter;

public class ItemLetter extends ItemInventoried {

	public static class LetterInventory extends ItemInventory implements IErrorSource, IHintSource {

		ILetter letter;

		public LetterInventory(ItemStack itemstack) {
			super(ItemLetter.class, 0, itemstack);

			// Set an uid to identify the itemstack on SMP
			setUID(true);

			readFromNBT(itemstack.getTagCompound());
		}

		public ILetter getLetter() {
			return this.letter;
		}

		@Override
		public void onGuiSaved(EntityPlayer player) {
			super.onGuiSaved(player);

			if (parent == null) {
				return;
			}

			// Already delivered mails can't be made usable anymore.
			int state = getState(parent.getItemDamage());
			if (state >= 2) {
				if (state == 2 && letter.countAttachments() <= 0) {
					parent.setItemDamage(encodeMeta(3, getSize(parent.getItemDamage())));
				}
				return;
			}

			int type = getType(letter);

			if (parent != null && letter.isMailable() && letter.isPostPaid()) {
				parent.setItemDamage(encodeMeta(1, type));
			} else {
				parent.setItemDamage(encodeMeta(0, type));
			}
		}

		@Override
		public void readFromNBT(NBTTagCompound nbttagcompound) {

			if (nbttagcompound == null) {
				return;
			}

			letter = new Letter(nbttagcompound);
		}

		@Override
		public void writeToNBT(NBTTagCompound nbttagcompound) {
			letter.writeToNBT(nbttagcompound);
		}

		@Override
		public ItemStack decrStackSize(int i, int j) {
			return letter.decrStackSize(i, j);
		}

		@Override
		public void setInventorySlotContents(int i, ItemStack itemstack) {
			letter.setInventorySlotContents(i, itemstack);
		}

		@Override
		public ItemStack getStackInSlot(int i) {
			return letter.getStackInSlot(i);
		}

		@Override
		public int getSizeInventory() {
			return letter.getSizeInventory();
		}

		@Override
		public String getInventoryName() {
			return letter.getInventoryName();
		}

		@Override
		public int getInventoryStackLimit() {
			return letter.getInventoryStackLimit();
		}

		@Override
		public void markDirty() {
			letter.markDirty();
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer entityplayer) {
			return letter.isUseableByPlayer(entityplayer);
		}

		@Override
		public ItemStack getStackInSlotOnClosing(int slot) {
			return letter.getStackInSlotOnClosing(slot);
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			if (letter.isProcessed()) {
				return false;
			} else if (GuiUtil.isIndexInRange(slotIndex, Letter.SLOT_POSTAGE_1, Letter.SLOT_POSTAGE_COUNT)) {
				Item item = itemStack.getItem();
				return item instanceof ItemStamps;
			} else if (GuiUtil.isIndexInRange(slotIndex, Letter.SLOT_ATTACHMENT_1, Letter.SLOT_ATTACHMENT_COUNT)) {
				return !ForestryItem.letters.isItemEqual(itemStack);
			}
			return false;
		}

		// / IERRORSOURCE
		@Override
		public ImmutableSet<IErrorState> getErrorStates() {

			ImmutableSet.Builder<IErrorState> errorStates = ImmutableSet.builder();

			if (!letter.hasRecipient()) {
				errorStates.add(EnumErrorCode.NORECIPIENT);
			}

			if (!letter.isProcessed() && !letter.isPostPaid()) {
				errorStates.add(EnumErrorCode.NOTPOSTPAID);
			}

			return errorStates.build();
		}

		/* IHINTSOURCE */
		@Override
		public boolean hasHints() {
			return Config.hints.get("letter") != null && Config.hints.get("letter").length > 0;
		}

		@Override
		public String[] getHints() {
			return Config.hints.get("letter");
		}

	}

	public ItemLetter() {
		super();
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (Proxies.common.isSimulating(world)) {
			if (itemstack.stackSize == 1) {
				entityplayer.openGui(ForestryAPI.instance, GuiId.LetterGUI.ordinal(), world, (int) entityplayer.posX, (int) entityplayer.posY, (int) entityplayer.posZ);
			} else {
				entityplayer.addChatMessage(new ChatComponentTranslation("for.chat.mail.wrongstacksize"));
			}
		}

		return itemstack;
	}

	@Override
	public boolean getShareTag() {
		return true;
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	private IIcon[][] icons;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		icons = new IIcon[3][4];
		for (int i = 0; i < 3; i++) {
			icons[i][0] = TextureManager.getInstance().registerTex(register, "mail/letter." + i + ".fresh");
			icons[i][1] = TextureManager.getInstance().registerTex(register, "mail/letter." + i + ".stamped");
			icons[i][2] = TextureManager.getInstance().registerTex(register, "mail/letter." + i + ".opened");
			icons[i][3] = TextureManager.getInstance().registerTex(register, "mail/letter." + i + ".emptied");
		}

	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int damage) {

		int state = getState(damage);
		int size = getSize(damage);

		return icons[size][state];
	}

	public static int encodeMeta(int state, int size) {
		int meta = size << 4;
		meta |= state;
		return meta;
	}

	public static int getState(int meta) {
		return meta & 0x0f;
	}

	public static int getSize(int meta) {
		return meta >> 4;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag) {
		NBTTagCompound nbttagcompound = itemstack.getTagCompound();
		if (nbttagcompound == null) {
			list.add("<" + StringUtil.localize("gui.blank") + ">");
			return;
		}

		ILetter letter = new Letter(nbttagcompound);
		letter.addTooltip(list);
	}

	public static int getType(ILetter letter) {
		int count = letter.countAttachments();

		if (count > 5) {
			return 2;
		} else if (count > 1) {
			return 1;
		} else {
			return 0;
		}
	}

}
