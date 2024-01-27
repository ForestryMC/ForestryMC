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
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

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
	public boolean place(LevelAccessor world, RandomSource rand, BlockPos pos, boolean forced) {
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
			DiscreteVoxelShape voxelshapepart = this.updateLeaves(world, contour);
			StructureTemplate.updateShapeAtEdge(world, 3, voxelshapepart, contour.boundingBox.minX(), contour.boundingBox.minY(), contour.boundingBox.minZ());
			return true;
		}

		return false;
	}

	@Nullable
	private static GameProfile getOwner(LevelAccessor world, BlockPos pos) {
		TileTreeContainer tile = TileUtil.getTile(world, pos, TileTreeContainer.class);
		if (tile == null) {
			return null;
		}
		return tile.getOwnerHandler().getOwner();
	}

	public void preGenerate(LevelAccessor world, RandomSource rand, BlockPos startPos) {

	}

	/**
	 * Copied vanilla logic from TreeFeature#updateLeaves
	 */
	private DiscreteVoxelShape updateLeaves(LevelAccessor world, TreeContour.Impl contour) {
		BoundingBox boundingBox = contour.boundingBox;
		List<Set<BlockPos>> list = Lists.newArrayList();
		DiscreteVoxelShape voxelshapepart = new BitSetDiscreteVoxelShape(boundingBox.getXSpan(), boundingBox.getYSpan(), boundingBox.getZSpan());
		int i = 6;

		for (int j = 0; j < 6; ++j) {
			list.add(Sets.newHashSet());
		}

		BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

		/*for(BlockPos blockpos : Lists.newArrayList(decoratedBlocks)) {
			if (boundingBox.isInside(blockpos)) {
				voxelshapepart.setFull(blockpos.getX() - boundingBox.x0, blockpos.getY() - boundingBox.y0, blockpos.getZ() - boundingBox.z0, true, true);
			}
		}*/

		for (BlockPos blockpos1 : Lists.newArrayList(contour.leavePositions)) {
			if (boundingBox.isInside(blockpos1)) {
				voxelshapepart.fill(blockpos1.getX() - boundingBox.minX(), blockpos1.getY() - boundingBox.minY(), blockpos1.getZ() - boundingBox.minZ());
			}

			for (Direction direction : Direction.values()) {
				blockpos$mutable.setWithOffset(blockpos1, direction);
				if (!contour.leavePositions.contains(blockpos$mutable)) {
					BlockState blockstate = world.getBlockState(blockpos$mutable);
					if (blockstate.hasProperty(BlockStateProperties.DISTANCE)) {
						list.get(0).add(blockpos$mutable.immutable());
						// TreeFeature.setBlockKnownShape(world, blockpos$mutable, blockstate.setValue(BlockStateProperties.DISTANCE, 1));
						if (boundingBox.isInside(blockpos$mutable)) {
							voxelshapepart.fill(blockpos$mutable.getX() - boundingBox.minX(), blockpos$mutable.getY() - boundingBox.minY(), blockpos$mutable.getZ() - boundingBox.minZ());
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
					voxelshapepart.fill(blockpos2.getX() - boundingBox.minX(), blockpos2.getY() - boundingBox.minY(), blockpos2.getZ() - boundingBox.minZ());
				}

				for (Direction direction1 : Direction.values()) {
					blockpos$mutable.setWithOffset(blockpos2, direction1);
					if (!set.contains(blockpos$mutable) && !set1.contains(blockpos$mutable)) {
						BlockState blockstate1 = world.getBlockState(blockpos$mutable);
						if (blockstate1.hasProperty(BlockStateProperties.DISTANCE)) {
							int k = blockstate1.getValue(BlockStateProperties.DISTANCE);
							if (k > l + 1) {
								BlockState blockstate2 = blockstate1.setValue(BlockStateProperties.DISTANCE, Integer.valueOf(l + 1));
								// TreeFeature.setBlockKnownShape(world, blockpos$mutable, blockstate2);
								if (boundingBox.isInside(blockpos$mutable)) {
									voxelshapepart.fill(blockpos$mutable.getX() - boundingBox.minX(), blockpos$mutable.getY() - boundingBox.minY(), blockpos$mutable.getZ() - boundingBox.minZ());
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

	protected abstract Set<BlockPos> generateTrunk(LevelAccessor world, RandomSource rand, TreeBlockTypeLog wood, BlockPos startPos);

	protected abstract void generateLeaves(LevelAccessor world, RandomSource rand, TreeBlockTypeLeaf leaf, TreeContour contour, BlockPos startPos);

	protected abstract void generateExtras(LevelAccessor world, RandomSource rand, BlockPos startPos);

	@Nullable
	public abstract BlockPos getValidGrowthPos(LevelAccessor world, BlockPos pos);

	public void clearSaplings(LevelAccessor world, BlockPos genPos) {
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
