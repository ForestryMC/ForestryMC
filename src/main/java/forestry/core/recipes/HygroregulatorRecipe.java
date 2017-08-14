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

import net.minecraftforge.fluids.FluidStack;

public class HygroregulatorRecipe {
	public final FluidStack liquid;
	public final int transferTime;
	public final float humidChange;
	public final float tempChange;

	public HygroregulatorRecipe(FluidStack liquid, int transferTime, float humidChange, float tempChange) {
		this.liquid = liquid;
		this.transferTime = transferTime;
		this.humidChange = humidChange;
		this.tempChange = tempChange;
	}
}
