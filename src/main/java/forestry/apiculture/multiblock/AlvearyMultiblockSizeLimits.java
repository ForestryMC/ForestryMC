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
package forestry.apiculture.multiblock;

import forestry.core.multiblock.IMultiblockSizeLimits;

class AlvearyMultiblockSizeLimits implements IMultiblockSizeLimits {

	public static final AlvearyMultiblockSizeLimits instance = new AlvearyMultiblockSizeLimits();

	private AlvearyMultiblockSizeLimits() {

	}

	@Override
	public int getMinimumNumberOfBlocksForAssembledMachine() {
		return 27;
	}

	@Override
	public int getMaximumXSize() {
		return 3;
	}

	@Override
	public int getMaximumZSize() {
		return 3;
	}

	@Override
	public int getMaximumYSize() {
		return 3;
	}

	@Override
	public int getMinimumXSize() {
		return 3;
	}

	@Override
	public int getMinimumYSize() {
		return 3;
	}

	@Override
	public int getMinimumZSize() {
		return 3;
	}
}
