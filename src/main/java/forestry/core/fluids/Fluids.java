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
package forestry.core.fluids;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import forestry.core.render.TextureManager;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum Fluids {

	WATER, LAVA, FUEL, BIOMASS, BIOFUEL, CREOSOTE, STEAM, BIOETHANOL,
	COAL, PYROTHEUM, HONEY, MILK, JUICE, ICE, GLASS, OIL, SEEDOIL, SHORT_MEAD("short.mead");

	private static final List<String> myLiquids = new ArrayList<String>(values().length);

	private final String tag;

	private Fluids() {
		tag = name().toLowerCase(Locale.ENGLISH);
	}

	private Fluids(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	public Fluid get() {
		return FluidRegistry.getFluid(tag);
	}

	public void register() {
		Fluid fluid = get();
		if (fluid == null) {
			fluid = new Fluid(tag);
			FluidRegistry.registerFluid(fluid);
			myLiquids.add(tag);
		}
	}

	/**
	 * Gets a FluidStack filled with mb milliBuckets worth of Fluid.
	 */
	public FluidStack get(int mb) {
		return FluidRegistry.getFluidStack(tag, mb);
	}

	public boolean is(Fluid fluid) {
		return get() == fluid;
	}

	public boolean is(FluidStack fluidStack) {
		return fluidStack != null && get() == fluidStack.getFluid();
	}

	public boolean isContained(ItemStack containerStack) {
		return containerStack != null && FluidHelper.containsFluid(containerStack, get());
	}

	public static boolean areEqual(Fluid fluid, FluidStack fluidStack) {
		if (fluidStack != null && fluid == fluidStack.getFluid())
			return true;
		return fluid == null && fluidStack == null;
	}

	public static void resetFluidIcons(IIconRegister register) {
		for (String fluidString : myLiquids) {
			Fluid fluid = FluidRegistry.getFluid(fluidString);
			IIcon icon = TextureManager.getInstance().registerTex(register, "liquid/" + fluid.getName());
			fluid.setIcons(icon);
		}
	}

}
