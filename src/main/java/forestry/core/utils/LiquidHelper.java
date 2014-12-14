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
package forestry.core.utils;

import java.util.LinkedList;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.RecipeManagers;
import forestry.core.config.ForestryItem;
import forestry.core.render.TextureManager;

public class LiquidHelper {

	private static final LinkedList<String> myLiquids = new LinkedList<String>();

	public static boolean isEmptyLiquidData() {
		return FluidContainerRegistry.getRegisteredFluidContainerData().length <= 0;
	}

	public static Fluid getOrCreateLiquid(String ident) {
		if (!FluidRegistry.isFluidRegistered(ident)) {
			Fluid fluid = new Fluid(ident);
			FluidRegistry.registerFluid(fluid);
			myLiquids.add(ident);
		}
		return FluidRegistry.getFluid(ident);
	}

	public static FluidStack getLiquid(String name, int amount) {
		return FluidRegistry.getFluidStack(name, amount);
	}

	public static void injectLiquidContainer(String name, int volume, ItemStack filled, ItemStack empty) {
		injectLiquidContainer(name, volume, filled, empty, null, 0);
	}

	public static void injectWaxContainer(String name, int volume, ItemStack filled, ItemStack empty) {
		injectLiquidContainer(name, volume, filled, empty, ForestryItem.beeswax.getItemStack(), 10);
	}

	public static void injectRefractoryContainer(String name, int volume, ItemStack filled, ItemStack empty) {
		injectLiquidContainer(name, volume, filled, empty, ForestryItem.refractoryWax.getItemStack(), 10);
	}

	public static void injectTinContainer(String name, int volume, ItemStack filled, ItemStack empty) {
		injectLiquidContainer(name, volume, filled, empty, ForestryItem.ingotTin.getItemStack(), 5);
	}

	public static void injectLiquidContainer(String name, int volume, ItemStack filled, ItemStack empty, ItemStack remnant, int chance) {
		FluidStack contained = FluidRegistry.getFluidStack(name, volume);
		if (contained == null)
			throw new IllegalArgumentException(String.format("Attempted to inject a liquid container for the non-existent liquid '%s'.", name));

		FluidContainerData container = new FluidContainerData(contained, filled, empty);
		FluidContainerRegistry.registerFluidContainer(container);

		if (RecipeManagers.squeezerManager != null)
			if (!container.filledContainer.getItem().hasContainerItem(container.filledContainer))
				if (remnant != null)
					RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { container.filledContainer }, container.fluid, remnant, chance);
				else
					RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { container.filledContainer }, container.fluid);
	}

	public static void resetLiquidIcons(IIconRegister register) {
		for (String fluidString : myLiquids) {
			Fluid fluid = FluidRegistry.getFluid(fluidString);
			IIcon icon = TextureManager.getInstance().registerTex(register, "liquid/" + fluid.getName());
			fluid.setIcons(icon);
		}
	}
}
