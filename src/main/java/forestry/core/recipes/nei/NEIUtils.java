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
package forestry.core.recipes.nei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidContainerItem;

public class NEIUtils {

	public static String translate(String unlocalized) {
		return StatCollector.translateToLocal("neiintegration." + unlocalized);
	}

	public static List<ItemStack> getItemVariations(ItemStack base) {
		List<ItemStack> variations = new ArrayList<>();
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
}
