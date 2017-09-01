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

import forestry.apiculture.blocks.BlockCandle;
import forestry.core.items.IColoredItem;
import forestry.core.items.ItemBlockForestry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockCandle extends ItemBlockForestry<BlockCandle> implements IColoredItem {

	public ItemBlockCandle(BlockCandle block) {
		super(block);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemstack(ItemStack stack, int pass) {
		int value = 0xffffff;
		if (pass == 1 && stack.getTagCompound() != null) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey(BlockCandle.colourTagName)) {
				value = tag.getInteger(BlockCandle.colourTagName);
			}
		}
		return value;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		String value = getBlock().getUnlocalizedName();
		if (itemStack.getTagCompound() != null && itemStack.getTagCompound().hasKey(BlockCandle.colourTagName)) {
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
