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
package forestry.farming.items;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.utils.ItemTooltipUtil;
import forestry.core.utils.Translator;
import forestry.farming.models.EnumFarmBlockTexture;

public class ItemBlockFarm extends ItemBlock {

	public ItemBlockFarm(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		if (GuiScreen.isShiftKeyDown()) {
			tooltip.add(Translator.translateToLocal("tile.for.ffarm.tooltip"));
			if (stack.getTagCompound() == null) {
				return;
			}
			EnumFarmBlockTexture texture = EnumFarmBlockTexture.getFromCompound(stack.getTagCompound());

			tooltip.add(Translator.translateToLocal("tile.for.ffarm.material.tooltip") + texture.getFormatting() + TextFormatting.ITALIC + " " + texture.getName());
		} else {
			ItemTooltipUtil.addShiftInformation(stack, world, tooltip, flag);
		}
	}

	@Override
	public String getTranslationKey(ItemStack itemstack) {
		return super.getTranslationKey(itemstack) + "." + itemstack.getItemDamage();
	}
}
