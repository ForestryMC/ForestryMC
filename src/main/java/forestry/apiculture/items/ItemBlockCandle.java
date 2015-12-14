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

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.apiculture.blocks.BlockCandle;
import forestry.core.items.ItemBlockForestry;

public class ItemBlockCandle extends ItemBlockForestry {

	public ItemBlockCandle(Block block) {
		super(block);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int pass) {
		int value = 0xffffff;
		if (pass == 1 && stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey("colour")) {
				value = tag.getInteger("colour");
			}
		}
		return value;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		String value = getBlock().getUnlocalizedName();
		if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey(BlockCandle.colourTagName)) {
			value = value + ".dyed";
		}

		if (BlockCandle.isLit(itemStack)) {
			value = value + ".lit";
		} else {
			value = value + ".stump";
		}
		return value;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int meta, int pass) {
		return this.getBlock().getIcon((meta != 0) ? 8 : 0, pass);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getRenderPasses(int metadata) {
		return 2;
	}

}
