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
package forestry.core.recipes;

import com.google.common.base.Preconditions;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IHygroregulatorRecipe;

public class HygroregulatorRecipe implements IHygroregulatorRecipe {
	private final FluidStack liquid;
	private final int transferTime;
	private final float humidChange;
	private final float tempChange;

	public HygroregulatorRecipe(FluidStack liquid, int transferTime, float humidChange, float tempChange) {
		Preconditions.checkNotNull(liquid);
		Preconditions.checkArgument(transferTime > 0);
		this.liquid = liquid;
		this.transferTime = transferTime;
		this.humidChange = humidChange;
		this.tempChange = tempChange;
	}

	@Override
	public FluidStack getResource() {
		return liquid;
	}

	@Override
	public int getTransferTime() {
		return transferTime;
	}

	@Override
	public float getHumidChange() {
		return humidChange;
	}

	@Override
	public float getTempChange() {
		return tempChange;
	}
}
