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
package forestry.arboriculture.worldgen;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.world.ITreeGenData;

public class TreeBlockTypeLog implements ITreeBlockType {
	private final ITreeGenData tree;
	private EnumFacing facing = EnumFacing.UP;

	public TreeBlockTypeLog(ITreeGenData tree) {
		this.tree = tree;
	}

	@Override
	public void setDirection(EnumFacing facing) {
		this.facing = facing;
	}

	@Override
	public boolean setBlock(World world, BlockPos pos) {
		return tree.setLogBlock(world, pos, facing);
	}
}