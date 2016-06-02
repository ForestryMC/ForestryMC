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
package forestry.greenhouse.multiblock;

import forestry.core.multiblock.IMultiblockSizeLimits;

class GreenhouseMultiblockSizeLimits implements IMultiblockSizeLimits {
	public static final GreenhouseMultiblockSizeLimits instance = new GreenhouseMultiblockSizeLimits();

	private GreenhouseMultiblockSizeLimits() {

	}

	@Override
	public int getMinimumNumberOfBlocksForAssembledMachine() {
		return (5 * 5 * 5) - (3 * 3 * 3);
	}

	@Override
	public int getMaximumXSize() {
		return 64;
	}

	@Override
	public int getMaximumZSize() {
		return 64;
	}

	@Override
	public int getMaximumYSize() {
		return 150;
	}

	@Override
	public int getMinimumXSize() {
		return 5;
	}

	@Override
	public int getMinimumZSize() {
		return 5;
	}

	@Override
	public int getMinimumYSize() {
		return 5;
	}
}
