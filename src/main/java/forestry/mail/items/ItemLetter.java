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

import forestry.api.core.IModelManager;
import forestry.api.mail.ILetter;
import forestry.core.items.ItemWithGui;
import forestry.core.utils.Translator;
import forestry.mail.Letter;
import forestry.mail.LetterProperties;
import forestry.mail.gui.ContainerLetter;
import forestry.mail.gui.GuiLetter;
import forestry.mail.inventory.ItemInventoryLetter;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemLetter extends ItemWithGui {
	public ItemLetter() {
		setMaxStackSize(64);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack heldItem = playerIn.getHeldItem(handIn);
		if (heldItem.getCount() == 1) {
			return super.onItemRightClick(worldIn, playerIn, handIn);
		} else {
			playerIn.sendMessage(new TextComponentTranslation("for.chat.mail.wrongstacksize"));
			return ActionResult.newResult(EnumActionResult.FAIL, heldItem);
		}
	}

	@Override
	public boolean getShareTag() {
		return true;
	}

	/* MODELS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		LetterProperties.registerModel(item, manager);
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List<String> list, boolean flag) {
		super.addInformation(itemstack, player, list, flag);

		NBTTagCompound nbttagcompound = itemstack.getTagCompound();
		if (nbttagcompound == null) {
			list.add('<' + Translator.translateToLocal("for.gui.blank") + '>');
			return;
		}

		ILetter letter = new Letter(nbttagcompound);
		letter.addTooltip(list);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		LetterProperties.getSubItems(item, tab, subItems);
	}

	public List<ItemStack> getEmptiedLetters() {
		return LetterProperties.getEmptiedLetters(this);
	}

	@Override
	public Object getGui(EntityPlayer player, ItemStack heldItem, int data) {
		return new GuiLetter(player, new ItemInventoryLetter(player, heldItem));
	}

	@Override
	public Object getContainer(EntityPlayer player, ItemStack heldItem, int data) {
		return new ContainerLetter(player, new ItemInventoryLetter(player, heldItem));
	}
}
