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
package forestry.core.tiles;

import javax.annotation.Nonnull;

import forestry.api.core.ILocatable;

import net.minecraft.util.EnumFacing;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import forestry.core.fluids.ITankManager;

public interface ILiquidTankTile extends ILocatable, IFluidHandler {

	@Nonnull
	ITankManager getTankManager();

	@Override
	default int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		return getTankManager().fill(resource, doFill);
	}

	@Override
	default FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		return getTankManager().drain(resource, doDrain);
	}

	@Override
	default FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		return getTankManager().drain(maxDrain, doDrain);
	}

	@Override
	default boolean canFill(EnumFacing from, Fluid fluid) {
		return getTankManager().canFillFluidType(new FluidStack(fluid, Fluid.BUCKET_VOLUME));
	}

	@Override
	default boolean canDrain(EnumFacing from, Fluid fluid) {
		return getTankManager().canDrainFluidType(new FluidStack(fluid, Fluid.BUCKET_VOLUME));
	}

	@Override
	default FluidTankInfo[] getTankInfo(EnumFacing from) {
		IFluidTankProperties[] tankProperties = getTankManager().getTankProperties();
		FluidTankInfo[] tankInfos = new FluidTankInfo[tankProperties.length];
		for (int i = 0; i < tankProperties.length; i++) {
			IFluidTankProperties fluidTankProperties = tankProperties[i];
			tankInfos[i] = new FluidTankInfo(fluidTankProperties.getContents(), fluidTankProperties.getCapacity());
		}
		return tankInfos;
	}
}
