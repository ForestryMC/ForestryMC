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
package forestry.core.genetics;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import forestry.core.genetics.research.ResearchNote;
import forestry.core.items.ItemForestry;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

public class ItemResearchNote extends ItemForestry {

	public ItemResearchNote() {
		super();
		setCreativeTab(null);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		ResearchNote note = ResearchNote.create(itemstack.getTagCompound());
		String researcherName;
		if (note == null) {
			researcherName = "Sengir";
		} else {
			researcherName = note.getResearcher().getName();
		}

		return StringUtil.localizeAndFormatRaw(getUnlocalizedName(itemstack) + ".name", researcherName);
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List<String> list, boolean advanced) {
		ResearchNote note = ResearchNote.create(itemstack.getTagCompound());
		if (note != null) {
			note.addTooltip(list);
		} else {
			list.add(EnumChatFormatting.ITALIC + EnumChatFormatting.RED.toString() + StringUtil.localize("researchNote.error.0"));
			list.add(StringUtil.localize("researchNote.error.1"));
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (world.isRemote) {
			return itemstack;
		}

		ResearchNote note = ResearchNote.create(itemstack.getTagCompound());
		if (note != null && note.registerResults(world, entityplayer)) {
			entityplayer.inventory.decrStackSize(entityplayer.inventory.currentItem, 1);
			// Notify player that his inventory has changed.
			Proxies.net.inventoryChangeNotify(entityplayer);
		}

		return itemstack;
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int j) {
		return 0xffe8a5;
	}
}
