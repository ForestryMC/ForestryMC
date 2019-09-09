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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.ItemGroups;
import forestry.core.utils.ItemTooltipUtil;
import forestry.farming.models.EnumFarmBlockTexture;

public class ItemBlockFarm extends BlockItem {

	public ItemBlockFarm(Block block) {
		super(block, new Item.Properties().group(ItemGroups.tabAgriculture));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		if (Screen.hasShiftDown()) {
			tooltip.add(new TranslationTextComponent("block.forestry.ffarm.tooltip"));
			if (stack.getTag() == null) {
				return;
			}
			EnumFarmBlockTexture texture = EnumFarmBlockTexture.getFromCompound(stack.getTag());

			tooltip.add(new TranslationTextComponent("block.forestry.ffarm.material.tooltip").setStyle((new Style()).setItalic(true).setColor(texture.getFormatting())).appendText(" " + texture.getName()));
		} else {
			ItemTooltipUtil.addShiftInformation(stack, world, tooltip, flag);
		}
	}

	@Override
	public String getTranslationKey(ItemStack itemstack) {
		return super.getTranslationKey(itemstack) + "." + 0;// TODO flatten itemstack.getItemDamage();
	}
}
