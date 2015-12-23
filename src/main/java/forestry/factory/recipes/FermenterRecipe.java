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
package forestry.factory.recipes;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFermenterRecipe;
import forestry.core.fluids.Fluids;

public class FermenterRecipe implements IFermenterRecipe {

	private final ItemStack resource;
	private final int fermentationValue;
	private final float modifier;
	private final Fluid output;
	private final FluidStack fluidResource;

	public FermenterRecipe(ItemStack resource, int fermentationValue, float modifier, Fluid output, FluidStack fluidResource) {
		// assume that fermenter recipes want to use Forestry's honey and not the legacy "fluid.honey"
		if (Fluids.LEGACY_HONEY.is(fluidResource)) {
			fluidResource = Fluids.HONEY.getFluid(fluidResource.amount);
		}

		if (resource == null) {
			throw new NullPointerException("Fermenter Resource cannot be null!");
		}

		if (output == null) {
			throw new NullPointerException("Fermenter Output cannot be null!");
		}

		if (fluidResource == null) {
			throw new NullPointerException("Fermenter Liquid cannot be null!");
		}

		this.resource = resource;
		this.fermentationValue = fermentationValue;
		this.modifier = modifier;
		this.output = output;
		this.fluidResource = fluidResource;
	}

	@Override
	public ItemStack getResource() {
		return resource;
	}

	@Override
	public FluidStack getFluidResource() {
		return fluidResource;
	}

	@Override
	public int getFermentationValue() {
		return fermentationValue;
	}

	@Override
	public float getModifier() {
		return modifier;
	}

	@Override
	public Fluid getOutput() {
		return output;
	}
}
