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
package forestry.core.render;

import javax.annotation.Nullable;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public class TankRenderInfo {
	public static final TankRenderInfo EMPTY = new TankRenderInfo(new FluidStack(FluidRegistry.WATER, 0), EnumTankLevel.EMPTY);

	@Nullable
	private final FluidStack fluidStack;
	private final EnumTankLevel level;

	public TankRenderInfo(IFluidTank fluidTank) {
		this(fluidTank.getFluid(), EnumTankLevel.rateTankLevel(fluidTank));
	}

	public TankRenderInfo(@Nullable FluidStack fluidStack, EnumTankLevel level) {
		this.fluidStack = fluidStack;
		this.level = level;
	}

	@Nullable
	public FluidStack getFluidStack() {
		return fluidStack;
	}

	public EnumTankLevel getLevel() {
		return level;
	}
}
