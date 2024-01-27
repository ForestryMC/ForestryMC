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

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.ItemGroupForestry;
import forestry.core.items.ItemBlockForestry;
import forestry.core.utils.ItemTooltipUtil;
import forestry.farming.blocks.BlockFarm;

public class ItemBlockFarm extends ItemBlockForestry<BlockFarm> {

	public ItemBlockFarm(BlockFarm block) {
		super(block, new Item.Properties().tab(ItemGroupForestry.tabForestry));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
		if (Screen.hasShiftDown()) {
			tooltip.add(Component.translatable("block.forestry.farm.tooltip").withStyle(ChatFormatting.GRAY));
			/*BlockFarm block = getBlock();
			EnumFarmMaterial material = block.getFarmMaterial();
			tooltip.add(new TranslationTextComponent("block.forestry.farm.material.tooltip").setStyle((new Style()).setItalic(true).setColor(material.getFormatting())).appendText(" " + WordUtils.capitalize(material.getName().replace("_", ""))));*/
		} else {
			ItemTooltipUtil.addShiftInformation(stack, world, tooltip, flag);
		}
	}

	@Override
	public String getDescriptionId() {
		BlockFarm block = getBlock();
		return "block.forestry.farm_" + block.getType().getSerializedName();
	}
}
