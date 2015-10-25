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
package forestry.farming.multiblock;

import forestry.core.multiblock.MultiblockControllerBase;
import net.minecraft.block.state.IBlockState;

public class TileFarmPlain extends TileFarm {

	@Override
	public void onMachineAssembled(MultiblockControllerBase controller) {
		super.onMachineAssembled(controller);

		// set band block meta
		int bandY = controller.getMaximumCoord().pos.getX() - 1;
		if (pos.getY() == bandY) {
			IBlockState state = worldObj.getBlockState(pos);
			this.worldObj.setBlockState(pos, state.getBlock().getStateFromMeta(1), 2);
		}
	}

	@Override
	public void onMachineBroken() {
		super.onMachineBroken();

		// set band block meta back to normal
		IBlockState state = worldObj.getBlockState(pos);
		this.worldObj.setBlockState(pos, state.getBlock().getStateFromMeta(0), 2);
	}
}
