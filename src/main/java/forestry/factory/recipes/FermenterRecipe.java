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

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFermenterRecipe;

public class FermenterRecipe implements IFermenterRecipe {

	private final ItemStack resource;
	@Nullable
	private final String resourceOreName;
	private final int fermentationValue;
	private final float modifier;
	private final Fluid output;
	private final FluidStack fluidResource;

	public FermenterRecipe(ItemStack resource, int fermentationValue, float modifier, Fluid output, FluidStack fluidResource) {
		Preconditions.checkNotNull(resource, "Fermenter Resource cannot be null!");
		Preconditions.checkArgument(!resource.isEmpty(), "Fermenter Resource item cannot be empty!");
		Preconditions.checkNotNull(output, "Fermenter Output cannot be null!");
		Preconditions.checkNotNull(fluidResource, "Fermenter Liquid cannot be null!");

		this.resource = resource;
		this.resourceOreName = null;
		this.fermentationValue = fermentationValue;
		this.modifier = modifier;
		this.output = output;
		this.fluidResource = fluidResource;
	}

	public FermenterRecipe(String resourceOreName, int fermentationValue, float modifier, Fluid output, FluidStack fluidResource) {
		Preconditions.checkNotNull(resourceOreName, "Fermenter Resource cannot be null!");
		Preconditions.checkArgument(!resourceOreName.isEmpty(), "Fermenter Resource ore name cannot be empty!");
		Preconditions.checkNotNull(output, "Fermenter Output cannot be null!");
		Preconditions.checkNotNull(fluidResource, "Fermenter Liquid cannot be null!");

		this.resource = ItemStack.EMPTY;
		this.resourceOreName = resourceOreName;
		this.fermentationValue = fermentationValue;
		this.modifier = modifier;
		this.output = output;
		this.fluidResource = fluidResource;
	}


	@Override
	public ItemStack getResource() {
		return resource;
	}

	@Nullable
	@Override
	public String getResourceOreName() {
		return resourceOreName;
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

	@Override
	public int compareTo(IFermenterRecipe o) {
		return !resource.isEmpty() ? -1 : 1;
	}
}
