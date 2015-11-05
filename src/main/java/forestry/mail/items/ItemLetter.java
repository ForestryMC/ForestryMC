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

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.mail.ILetter;
import forestry.core.items.ItemWithGui;
import forestry.core.network.GuiId;
import forestry.core.utils.StringUtil;
import forestry.mail.Letter;
import forestry.mail.LetterProperties;

public class ItemLetter extends ItemWithGui {
	public ItemLetter() {
		super(GuiId.LetterGUI);
		setMaxStackSize(64);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (!world.isRemote) {
			if (itemstack.stackSize == 1) {
				openGui(world, entityplayer);
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
	@Override
	public void registerIcons(IIconRegister register) {
		LetterProperties.registerIcons(register);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int damage) {
		return LetterProperties.getIconFromDamage(damage);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag) {
		NBTTagCompound nbttagcompound = itemstack.getTagCompound();
		if (nbttagcompound == null) {
			list.add('<' + StringUtil.localize("gui.blank") + '>');
			return;
		}

		ILetter letter = new Letter(nbttagcompound);
		letter.addTooltip(list);
	}
}
