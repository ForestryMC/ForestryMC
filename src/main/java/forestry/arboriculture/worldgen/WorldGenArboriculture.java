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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.mojang.authlib.GameProfile;
import forestry.api.world.ITreeGenData;
import forestry.arboriculture.blocks.BlockSapling;
import forestry.arboriculture.tiles.TileTreeContainer;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.TopDownBlockPosComparator;
import forestry.core.worldgen.WorldGenBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.TerrainGen;

public abstract class WorldGenArboriculture extends WorldGenBase {

	protected static final int minPodHeight = 3;

	protected final ITreeGenData tree;

	protected WorldGenArboriculture(ITreeGenData tree) {
		this.tree = tree;
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos, boolean forced) {
		if (!TerrainGen.saplingGrowTree(world, rand, pos)) {
			return false;
		}

		GameProfile owner = getOwner(world, pos);
		TreeBlockTypeLeaf leaf = new TreeBlockTypeLeaf(tree, owner);
		TreeBlockTypeLog wood = new TreeBlockTypeLog(tree);

		preGenerate(world, rand, pos);

		BlockPos genPos;
		if (forced) {
			genPos = pos;
		} else {
			genPos = getValidGrowthPos(world, pos);
		}

		if (genPos != null) {
			clearSaplings(world, genPos);
			List<BlockPos> branchEnds = new ArrayList<>(generateTrunk(world, rand, wood, genPos));
			branchEnds.sort(TopDownBlockPosComparator.INSTANCE);
			generateLeaves(world, rand, leaf, branchEnds, genPos);
			generateExtras(world, rand, genPos);
			return true;
		}

		return false;
	}

	@Nullable
	private static GameProfile getOwner(World world, BlockPos pos) {
		TileTreeContainer tile = TileUtil.getTile(world, pos, TileTreeContainer.class);
		if (tile == null) {
			return null;
		}
		return tile.getOwnerHandler().getOwner();
	}

	public void preGenerate(World world, Random rand, BlockPos startPos) {

	}

	/**
	 * Generate the tree's trunk. Returns a list of positions of branch ends for leaves to generate at.
	 */

	protected abstract Set<BlockPos> generateTrunk(World world, Random rand, TreeBlockTypeLog wood, BlockPos startPos);

	protected abstract void generateLeaves(World world, Random rand, TreeBlockTypeLeaf leaf, List<BlockPos> branchEnds, BlockPos startPos);

	protected abstract void generateExtras(World world, Random rand, BlockPos startPos);

	@Nullable
	public abstract BlockPos getValidGrowthPos(World world, BlockPos pos);

	public void clearSaplings(World world, BlockPos genPos) {
		int treeGirth = tree.getGirth();
		for (int x = 0; x < treeGirth; x++) {
			for (int z = 0; z < treeGirth; z++) {
				BlockPos saplingPos = genPos.add(x, 0, z);
				if (world.getBlockState(saplingPos).getBlock() instanceof BlockSapling) {
					world.setBlockToAir(saplingPos);
				}
			}
		}
	}

	public boolean hasPods() {
		return tree.allowsFruitBlocks();
	}

}
