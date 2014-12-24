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

import java.util.Locale;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum Fluids {

	WATER, LAVA,
	FUEL, OIL, BIOFUEL,
	CREOSOTE, STEAM,
	COAL, PYROTHEUM,
	BIOMASS, BIOETHANOL, LEGACY_HONEY("honey"), HONEY("for.honey"), MILK, JUICE, ICE, GLASS, SEEDOIL, SHORT_MEAD("short.mead");

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

	public Fluid getFluid() {
		return FluidRegistry.getFluid(tag);
	}

	/**
	 * Gets a FluidStack filled with mb milliBuckets worth of Fluid.
	 */
	public FluidStack getFluid(int mb) {
		return FluidRegistry.getFluidStack(tag, mb);
	}

	public boolean is(Fluid fluid) {
		return getFluid() == fluid;
	}

	public boolean is(FluidStack fluidStack) {
		return fluidStack != null && getFluid() == fluidStack.getFluid();
	}

	public boolean isContained(ItemStack containerStack) {
		return containerStack != null && FluidHelper.containsFluid(containerStack, getFluid());
	}

	public static boolean areEqual(Fluid fluid, FluidStack fluidStack) {
		if (fluidStack != null && fluid == fluidStack.getFluid())
			return true;
		return fluid == null && fluidStack == null;
	}
}
