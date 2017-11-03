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
package forestry.greenhouse.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.multiblock.IGreenhouseController;
import forestry.api.multiblock.IMultiblockController;
import forestry.greenhouse.ModuleGreenhouse;
import forestry.greenhouse.blocks.BlockGreenhouse;
import forestry.greenhouse.blocks.BlockGreenhouseType;

public class TileGreenhousePlain extends TileGreenhouse {
	@Override
	public void onMachineAssembled(IMultiblockController multiblockController, BlockPos minCoord, BlockPos maxCoord) {
		super.onMachineAssembled(multiblockController, minCoord, maxCoord);

		IGreenhouseController greenhouseController = (IGreenhouseController) multiblockController;

		// set border block state
		int bandY = maxCoord.getY();
		if (getPos().getY() == bandY) {
			BlockGreenhouseType type = BlockGreenhouseType.BORDER;
			if (getPos().equals(greenhouseController.getCenterCoordinates())) {
				type = BlockGreenhouseType.BORDER_CENTER;
			}
			this.world.setBlockState(getPos(), ModuleGreenhouse.getBlocks().greenhouseBlock.getDefaultState().withProperty(BlockGreenhouse.TYPE, type), 2);
		}
	}

	@Override
	public void onMachineBroken() {
		super.onMachineBroken();

		// set border block state back to normal
		this.world.setBlockState(getPos(), ModuleGreenhouse.getBlocks().greenhouseBlock.getDefaultState().withProperty(BlockGreenhouse.TYPE, BlockGreenhouseType.PLAIN), 2);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}
}
