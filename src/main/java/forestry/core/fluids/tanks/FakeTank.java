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
package forestry.core.fluids.tanks;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class FakeTank extends StandardTank {

	public static final FakeTank INSTANCE = new FakeTank();
	public static final FakeTank[] ARRAY = new FakeTank[]{INSTANCE};
	public static final FluidTankInfo[] INFO = new FluidTankInfo[]{INSTANCE.getInfo()};

	private FakeTank() {
		super(1);
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		return null;
	}

}
