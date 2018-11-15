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
import java.util.Random;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.world.ITreeGenData;

public class TreeBlockTypeLeaf implements ITreeBlockType {
	private final ITreeGenData tree;
	@Nullable
	private final GameProfile owner;
	@Nullable
	private final Random rand;

	public TreeBlockTypeLeaf(ITreeGenData tree, @Nullable GameProfile owner) {
		this(tree, owner, null);
	}

	public TreeBlockTypeLeaf(ITreeGenData tree, @Nullable GameProfile owner, @Nullable Random rand) {
		this.tree = tree;
		this.owner = owner;
		this.rand = rand;
	}

	@Override
	public void setDirection(EnumFacing facing) {

	}

	@Override
	public boolean setBlock(World world, BlockPos pos) {
		return tree.setLeaves(world, owner, pos, rand == null ? world.rand : rand);
	}
}
