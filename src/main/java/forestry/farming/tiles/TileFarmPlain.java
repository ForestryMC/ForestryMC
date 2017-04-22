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
package forestry.farming.tiles;

import forestry.api.multiblock.IMultiblockController;
import net.minecraft.util.math.BlockPos;

public class TileFarmPlain extends TileFarm {
	@Override
	public void onMachineAssembled(IMultiblockController multiblockController, BlockPos minCoord, BlockPos maxCoord) {
		super.onMachineAssembled(multiblockController, minCoord, maxCoord);

		// set band block meta
		int bandY = maxCoord.getY() - 1;
		if (getPos().getY() == bandY) {
			this.world.setBlockState(getPos(), getBlockType().getStateFromMeta(1), 2);
		}
	}

	@Override
	public void onMachineBroken() {
		super.onMachineBroken();

		// set band block meta back to normal
		this.world.setBlockState(getPos(), getBlockType().getDefaultState(), 2);
	}
}
