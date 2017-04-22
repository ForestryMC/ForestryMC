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
import forestry.api.recipes.IStillRecipe;
import net.minecraftforge.fluids.FluidStack;

public class StillRecipe implements IStillRecipe {
	private final int timePerUnit;
	private final FluidStack input;
	private final FluidStack output;

	public StillRecipe(int timePerUnit, FluidStack input, FluidStack output) {
		Preconditions.checkNotNull(input, "Still recipes need an input. Input was null.");
		Preconditions.checkNotNull(output, "Still recipes need an output. Output was null.");

		this.timePerUnit = timePerUnit;
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
