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
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.Tabs;
import forestry.core.gui.ContainerAlyzer;
import forestry.core.gui.GuiAlyzer;
import forestry.core.inventory.ItemInventoryAlyzer;
import forestry.core.utils.Translator;

public class ItemAlyzer extends ItemWithGui {
	public ItemAlyzer() {
		setCreativeTab(Tabs.tabApiculture);
	}

	@Override
	public void openGui(EntityPlayer entityplayer) {
		super.openGui(entityplayer);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(EntityPlayer player, ItemStack heldItem, int data) {
		return new GuiAlyzer(player, new ItemInventoryAlyzer(player, heldItem));
	}

	@Override
	public Container getContainer(EntityPlayer player, ItemStack heldItem, int data) {
		return new ContainerAlyzer(new ItemInventoryAlyzer(player, heldItem), player);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
		super.addInformation(stack, world, tooltip, advanced);
		int charges = 0;
		NBTTagCompound compound = stack.getTagCompound();
		if (compound != null) {
			charges = compound.getInteger("Charges");
		}
		tooltip.add(TextFormatting.GOLD + Translator.translateToLocalFormatted(stack.getTranslationKey() + ".charges", charges));
	}
}
