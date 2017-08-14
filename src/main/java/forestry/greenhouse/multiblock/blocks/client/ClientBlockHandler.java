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
package forestry.greenhouse.multiblock.blocks.client;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import forestry.api.greenhouse.IGreenhouseBlock;
import forestry.api.greenhouse.IGreenhouseBlockHandler;
import forestry.api.greenhouse.IGreenhouseBlockStorage;
import forestry.greenhouse.multiblock.blocks.GreenhouseException;

public class ClientBlockHandler implements IGreenhouseBlockHandler<ClientBlock, ClientBlock> {

	private static final ClientBlockHandler INSTANCE = new ClientBlockHandler();

	public static ClientBlockHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public void onRemoveBlock(IGreenhouseBlockStorage storage, ClientBlock blockToRemove) {
	}

	@Override
	public ClientBlock createBlock(IGreenhouseBlockStorage storage, @Nullable ClientBlock root, @Nullable EnumFacing rootFacing, @Nullable BlockPos position) {
		return new ClientBlock(storage.getProvider(), position);
	}

	@Override
	public ClientBlock getBlock(IGreenhouseBlockStorage storage, BlockPos position) {
		IGreenhouseBlock logicBlock = storage.getBlock(position);
		if (logicBlock instanceof ClientBlock) {
			return (ClientBlock) logicBlock;
		}
		return null;
	}

	@Override
	public List<IGreenhouseBlock> checkNeighborBlocks(IGreenhouseBlockStorage storage, ClientBlock blockToCheck) throws GreenhouseException {
		return Collections.emptyList();
	}

	@Override
	public boolean onCheckPosition(IGreenhouseBlockStorage storage, ClientBlock rootBlock, BlockPos position, EnumFacing facing, IGreenhouseBlock block, List<IGreenhouseBlock> newBlocksToCheck) throws GreenhouseException {
		return false;
	}

	@Override
	public Class getBlockClass() {
		return ClientBlock.class;
	}
}
