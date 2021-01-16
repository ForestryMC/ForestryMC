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
package forestry.apiculture.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.ItemGroups;
import forestry.apiculture.blocks.BlockCandle;
import forestry.apiculture.blocks.BlockCandleWall;
import forestry.core.items.IColoredItem;
import forestry.core.items.ItemBlockWallForestry;

public class ItemBlockCandle extends ItemBlockWallForestry<BlockCandle, BlockCandleWall> implements IColoredItem {

	public ItemBlockCandle(BlockCandle block, BlockCandleWall wall) {
		super(block, wall, new Item.Properties().group(ItemGroups.tabApiculture));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int pass) {
		int value = 0xffffff;
		if (pass == 1 && stack.getTag() != null) {
			CompoundNBT tag = stack.getTag();
			if (tag.contains(BlockCandle.COLOUR_TAG_NAME)) {
				value = tag.getInt(BlockCandle.COLOUR_TAG_NAME);
			}
		}
		return value;
	}

	@Override
	public String getTranslationKey(ItemStack itemStack) {
		String value = getBlock().getTranslationKey();
		if (itemStack.getTag() != null && itemStack.getTag().contains(BlockCandle.COLOUR_TAG_NAME)) {
			value = value + ".dyed";
		}

		if (BlockCandle.isLit(itemStack)) {
			value = value + ".lit";
		} else {
			value = value + ".stump";
		}
		return value;
	}
}
