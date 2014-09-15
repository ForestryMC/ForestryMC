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

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.world.ITreeGenData;
import forestry.arboriculture.genetics.TreeHelper;
import forestry.core.worldgen.BlockType;

public class BlockTypeLeaf extends BlockType {

	private GameProfile owner;

	public BlockTypeLeaf(GameProfile owner, IAlleleTreeSpecies species) {
		super(TreeHelper.getBlockLeavesForAllele(species), 0);
		this.owner = owner;
	}

	@Override
	public void setBlock(World world, ITreeGenData tree, int x, int y, int z) {
		tree.setLeaves(world, owner, x, y, z);
	}
}
