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
package forestry.factory.recipes.nei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidContainerItem;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;

public class NEIUtils {

	public static String translate(String unlocalized) {
		return StatCollector.translateToLocal("neiintegration." + unlocalized);
	}

	public static List<ItemStack> getItemVariations(ItemStack base) {
		List<ItemStack> variations = new ArrayList<ItemStack>();
		base.getItem().getSubItems(base.getItem(), null, variations);
		Iterator<ItemStack> itr = variations.iterator();
		ItemStack stack;
		while (itr.hasNext()) {
			stack = itr.next();
			if (!base.isItemEqual(stack) || !stack.hasTagCompound()) {
				itr.remove();
			}
		}
		if (variations.isEmpty()) {
			return Collections.singletonList(base);
		}
		return variations;
	}

	public static FluidStack getFluidStack(ItemStack stack) {
		if (stack != null) {
			FluidStack fluidStack = null;
			if (stack.getItem() instanceof IFluidContainerItem) {
				fluidStack = ((IFluidContainerItem) stack.getItem()).getFluid(stack);
			}
			if (fluidStack == null) {
				fluidStack = FluidContainerRegistry.getFluidForFilledItem(stack);
			}
			if (fluidStack == null && Block.getBlockFromItem(stack.getItem()) instanceof IFluidBlock) {
				Fluid fluid = ((IFluidBlock) Block.getBlockFromItem(stack.getItem())).getFluid();
				if (fluid != null) {
					fluidStack = new FluidStack(fluid, 1000);
				}
			}
			return fluidStack;
		}
		return null;
	}

	public static boolean areFluidsSameType(FluidStack fluidStack1, FluidStack fluidStack2) {
		if (fluidStack1 == null || fluidStack2 == null) {
			return false;
		}
		return fluidStack1.getFluid() == fluidStack2.getFluid();
	}

	public static void setIngredientPermutationNBT(INBTMatchingCachedRecipe recipe, ItemStack ingredient) {
		Iterable<PositionedStack> ingredients = recipe.getIngredients();
		for (PositionedStack stack : ingredients) {
			for (int i = 0; i < stack.items.length; i++) {
				if (NEIServerUtils.areStacksSameTypeCrafting(ingredient, stack.items[i])) {
					stack.item = stack.items[i];
					stack.item.setItemDamage(ingredient.getItemDamage());

					if (ingredient.hasTagCompound()) {
						NBTTagCompound ingredientNbt = (NBTTagCompound) ingredient.getTagCompound().copy();
						stack.item.setTagCompound(ingredientNbt);
						if (recipe.preservesNBT()) {
							recipe.getResult().item.setTagCompound(ingredientNbt);
						}
					}

					stack.items = new ItemStack[] { stack.item };
					stack.setPermutationToRender(0);
					break;
				}
			}
		}
	}

	public static void setResultPermutationNBT(INBTMatchingCachedRecipe recipe, ItemStack result) {
		PositionedStack recipeResult = recipe.getResult();
		if (recipe.preservesNBT()) {
			NBTTagCompound resultNbt = result.getTagCompound();
			recipeResult.item.setTagCompound((NBTTagCompound) resultNbt.copy());
			Iterable<PositionedStack> ingredients = recipe.getIngredients();
			for (PositionedStack stack : ingredients) {
				if (stack.item.hasTagCompound()) {
					stack.item.setTagCompound((NBTTagCompound) resultNbt.copy());
				}

				stack.items = new ItemStack[] { stack.item };
				stack.setPermutationToRender(0);
			}
		}
	}

}
