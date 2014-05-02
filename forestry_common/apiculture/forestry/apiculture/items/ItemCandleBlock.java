/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.apiculture.gadgets.BlockCandle;
import forestry.core.items.ItemForestryBlock;

public class ItemCandleBlock extends ItemForestryBlock {

	public ItemCandleBlock(Block block) {
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
		String value = "tile.candle";
		if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey(BlockCandle.colourTagName)) {
			value = value + ".dyed";
		}

		if (BlockCandle.isLit(itemStack)) {
			value = value + ".lit";
		}
		else {
			value = value + ".stump";
		}
		return value;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int meta, int pass) {
		return ((BlockCandle)this.getBlock()).getTextureFromPassAndMeta((meta != 0) ? 8 : 0, pass);
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
