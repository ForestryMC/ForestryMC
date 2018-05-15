/*
 *******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 *******************************************************************************
 */
package forestry.core.recipes.json;

import javax.annotation.Nonnull;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.oredict.ShapedOreRecipe;

import forestry.core.utils.InventoryUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.farming.blocks.BlockFarm;

public class FarmBlockRecipe extends ShapedOreRecipe {

	public FarmBlockRecipe(ResourceLocation group, ItemStack result, CraftingHelper.ShapedPrimer primer) {
		super(group, result, primer);
	}

	@Override
	@Nonnull
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		ItemStack inputStack = ItemStack.EMPTY;
		ItemStack outputStack = output.copy();

		for (ItemStack stack : InventoryUtil.getStacks(inv)) {
			if (!stack.isEmpty()) {
				if (ItemStackUtil.getBlock(stack) instanceof BlockFarm) {
					inputStack = stack;
				}
			}
		}
		NBTTagCompound tag = inputStack.getTagCompound();
		if (inputStack.isEmpty() || !tag.hasKey("FarmBlock")) {
			return ItemStack.EMPTY;
		}
		if(!outputStack.hasTagCompound()) {
			outputStack.setTagCompound(new NBTTagCompound());
		}
		outputStack.getTagCompound().setInteger("FarmBlock", tag.getInteger("FarmBlock"));
		return outputStack;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}
}
