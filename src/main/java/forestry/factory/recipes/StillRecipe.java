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

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IStillRecipe;

public class StillRecipe implements IStillRecipe {
	private final int timePerUnit;
	private final FluidStack input;
	private final FluidStack output;

	public StillRecipe(int timePerUnit, FluidStack input, FluidStack output) {
		this.timePerUnit = timePerUnit;
		if (input == null) {
			throw new IllegalArgumentException("Still recipes need an input. Input was null.");
		}
		if (output == null) {
			throw new IllegalArgumentException("Still recipes need an output. Output was null.");
		}
		this.input = input;
		this.output = output;
	}

	@Override
	public int getCyclesPerUnit() {
		return timePerUnit;
	}

	@Override
	public FluidStack getInput() {
		return input;
	}

	@Override
	public FluidStack getOutput() {
		return output;
	}
}
