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

import net.minecraft.util.ChunkCoordinates;

import forestry.api.multiblock.IMultiblockController;

public class TileFarmPlain extends TileFarm {
	@Override
	public void onMachineAssembled(IMultiblockController multiblockController, ChunkCoordinates minCoord, ChunkCoordinates maxCoord) {
		super.onMachineAssembled(multiblockController, minCoord, maxCoord);

		// set band block meta
		int bandY = maxCoord.posY - 1;
		if (yCoord == bandY) {
			this.worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 1, 2);
		}
	}

	@Override
	public void onMachineBroken() {
		super.onMachineBroken();

		// set band block meta back to normal
		this.worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 2);
	}
}
