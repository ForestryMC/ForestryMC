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

import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorLogicSource;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.owner.IOwnerHandler;
import forestry.core.owner.IOwnedTile;
import forestry.core.multiblock.MultiblockTileEntityForestry;
import forestry.greenhouse.multiblock.MultiblockLogicGreenhouse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class TileGreenhouseDoor extends MultiblockTileEntityForestry<MultiblockLogicGreenhouse> implements IGreenhouseComponent.Door, IErrorLogicSource, IOwnedTile {

	public TileGreenhouseDoor() {
		super(new MultiblockLogicGreenhouse());
	}
	
	@Override
	public void onMachineAssembled(IMultiblockController multiblockController, BlockPos minCoord, BlockPos maxCoord) {
		worldObj.notifyBlockOfStateChange(getPos(), worldObj.getBlockState(pos).getBlock());
		markDirty();
	}

	@Override
	public void onMachineBroken() {
		worldObj.notifyBlockOfStateChange(getPos(), worldObj.getBlockState(pos).getBlock());
		markDirty();
	}
	
	@Override
	public IErrorLogic getErrorLogic() {
		return getMultiblockLogic().getController().getErrorLogic();
	}

	@Override
	public IOwnerHandler getOwnerHandler() {
		return getMultiblockLogic().getController().getOwnerHandler();
	}

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return null;
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return null;
	}

}
