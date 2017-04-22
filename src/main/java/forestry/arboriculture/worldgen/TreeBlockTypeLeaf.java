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

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;
import forestry.api.world.ITreeGenData;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TreeBlockTypeLeaf implements ITreeBlockType {
	private final ITreeGenData tree;
	@Nullable
	private final GameProfile owner;

	public TreeBlockTypeLeaf(ITreeGenData tree, @Nullable GameProfile owner) {
		this.tree = tree;
		this.owner = owner;
	}

	@Override
	public void setDirection(EnumFacing facing) {

	}

	@Override
	public boolean setBlock(World world, BlockPos pos) {
		return tree.setLeaves(world, owner, pos);
	}
}
