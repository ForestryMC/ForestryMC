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

import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.world.ITreeGenData;

public class TreeBlockTypeLeaf implements ITreeBlockType {

	private final GameProfile owner;

	public TreeBlockTypeLeaf(GameProfile owner) {
		this.owner = owner;
	}

	@Override
	public void setDirection(ForgeDirection facing) {

	}

	@Override
	public void setBlock(World world, ITreeGenData tree, int x, int y, int z) {
		tree.setLeaves(world, owner, x, y, z);
	}
}
