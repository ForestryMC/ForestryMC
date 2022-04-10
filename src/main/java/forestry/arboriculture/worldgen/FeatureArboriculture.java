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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.shapes.BitSetVoxelShapePart;
import net.minecraft.util.math.shapes.VoxelShapePart;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.template.Template;

import com.mojang.authlib.GameProfile;

import forestry.api.arboriculture.ITreeGenData;
import forestry.arboriculture.blocks.BlockSapling;
import forestry.arboriculture.tiles.TileTreeContainer;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.VectUtil;
import forestry.core.worldgen.FeatureBase;

public abstract class FeatureArboriculture extends FeatureBase {

	protected static final int minPodHeight = 3;

	protected final ITreeGenData tree;

	protected FeatureArboriculture(ITreeGenData tree) {
		this.tree = tree;
	}

	@Override
	public boolean place(IWorld world, Random rand, BlockPos pos, boolean forced) {
		GameProfile owner = getOwner(world, pos);
		TreeBlockTypeLeaf leaf = new TreeBlockTypeLeaf(tree, owner, rand);
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
			branchEnds.sort(VectUtil.TOP_DOWN_COMPARATOR);
			TreeContour.Impl contour = new TreeContour.Impl(branchEnds);
			generateLeaves(world, rand, leaf, contour, genPos);
			generateExtras(world, rand, genPos);
			updateLeaves(world, contour);
			VoxelShapePart voxelshapepart = this.updateLeaves(world, contour);
			Template.updateShapeAtEdge(world, 3, voxelshapepart, contour.boundingBox.x0, contour.boundingBox.y0, contour.boundingBox.z0);
			return true;
		}

		return false;
	}

	@Nullable
	private static GameProfile getOwner(IWorld world, BlockPos pos) {
		TileTreeContainer tile = TileUtil.getTile(world, pos, TileTreeContainer.class);
		if (tile == null) {
			return null;
		}
		return tile.getOwnerHandler().getOwner();
	}

	public void preGenerate(IWorld world, Random rand, BlockPos startPos) {

	}

	/**
	 * Copied vanilla logic from TreeFeature#updateLeaves
	 */
	private VoxelShapePart updateLeaves(IWorld world, TreeContour.Impl contour) {
		MutableBoundingBox boundingBox = contour.boundingBox;
		List<Set<BlockPos>> list = Lists.newArrayList();
		VoxelShapePart voxelshapepart = new BitSetVoxelShapePart(boundingBox.getXSpan(), boundingBox.getYSpan(), boundingBox.getZSpan());
		int i = 6;

		for (int j = 0; j < 6; ++j) {
			list.add(Sets.newHashSet());
		}

		BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

		/*for(BlockPos blockpos : Lists.newArrayList(decoratedBlocks)) {
			if (boundingBox.isInside(blockpos)) {
				voxelshapepart.setFull(blockpos.getX() - boundingBox.x0, blockpos.getY() - boundingBox.y0, blockpos.getZ() - boundingBox.z0, true, true);
			}
		}*/

		for (BlockPos blockpos1 : Lists.newArrayList(contour.leavePositions)) {
			if (boundingBox.isInside(blockpos1)) {
				voxelshapepart.setFull(blockpos1.getX() - boundingBox.x0, blockpos1.getY() - boundingBox.y0, blockpos1.getZ() - boundingBox.z0, true, true);
			}

			for (Direction direction : Direction.values()) {
				blockpos$mutable.setWithOffset(blockpos1, direction);
				if (!contour.leavePositions.contains(blockpos$mutable)) {
					BlockState blockstate = world.getBlockState(blockpos$mutable);
					if (blockstate.hasProperty(BlockStateProperties.DISTANCE)) {
						list.get(0).add(blockpos$mutable.immutable());
						TreeFeature.setBlockKnownShape(world, blockpos$mutable, blockstate.setValue(BlockStateProperties.DISTANCE, Integer.valueOf(1)));
						if (boundingBox.isInside(blockpos$mutable)) {
							voxelshapepart.setFull(blockpos$mutable.getX() - boundingBox.x0, blockpos$mutable.getY() - boundingBox.y0, blockpos$mutable.getZ() - boundingBox.z0, true, true);
						}
					}
				}
			}
		}

		for (int l = 1; l < 6; ++l) {
			Set<BlockPos> set = list.get(l - 1);
			Set<BlockPos> set1 = list.get(l);

			for (BlockPos blockpos2 : set) {
				if (boundingBox.isInside(blockpos2)) {
					voxelshapepart.setFull(blockpos2.getX() - boundingBox.x0, blockpos2.getY() - boundingBox.y0, blockpos2.getZ() - boundingBox.z0, true, true);
				}

				for (Direction direction1 : Direction.values()) {
					blockpos$mutable.setWithOffset(blockpos2, direction1);
					if (!set.contains(blockpos$mutable) && !set1.contains(blockpos$mutable)) {
						BlockState blockstate1 = world.getBlockState(blockpos$mutable);
						if (blockstate1.hasProperty(BlockStateProperties.DISTANCE)) {
							int k = blockstate1.getValue(BlockStateProperties.DISTANCE);
							if (k > l + 1) {
								BlockState blockstate2 = blockstate1.setValue(BlockStateProperties.DISTANCE, Integer.valueOf(l + 1));
								TreeFeature.setBlockKnownShape(world, blockpos$mutable, blockstate2);
								if (boundingBox.isInside(blockpos$mutable)) {
									voxelshapepart.setFull(blockpos$mutable.getX() - boundingBox.x0, blockpos$mutable.getY() - boundingBox.y0, blockpos$mutable.getZ() - boundingBox.z0, true, true);
								}

								set1.add(blockpos$mutable.immutable());
							}
						}
					}
				}
			}
		}

		return voxelshapepart;
	}

	/**
	 * Generate the tree's trunk. Returns a list of positions of branch ends for leaves to generate at.
	 */

	protected abstract Set<BlockPos> generateTrunk(IWorld world, Random rand, TreeBlockTypeLog wood, BlockPos startPos);

	protected abstract void generateLeaves(IWorld world, Random rand, TreeBlockTypeLeaf leaf, TreeContour contour, BlockPos startPos);

	protected abstract void generateExtras(IWorld world, Random rand, BlockPos startPos);

	@Nullable
	public abstract BlockPos getValidGrowthPos(IWorld world, BlockPos pos);

	public void clearSaplings(IWorld world, BlockPos genPos) {
		int treeGirth = tree.getGirth();
		for (int x = 0; x < treeGirth; x++) {
			for (int z = 0; z < treeGirth; z++) {
				BlockPos saplingPos = genPos.offset(x, 0, z);
				if (world.getBlockState(saplingPos).getBlock() instanceof BlockSapling) {
					world.setBlock(saplingPos, Blocks.AIR.defaultBlockState(), 18);
				}
			}
		}
	}

	public boolean hasPods() {
		return tree.allowsFruitBlocks();
	}

}
