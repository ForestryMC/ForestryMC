package forestry.arboriculture;

import forestry.core.utils.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/** Based on vanilla leaf decay, but accepts leaves that are connected further from a trunk */
public abstract class LeafDecayHelper {
	private static final short SUSTAINS_LEAVES = 0;
	private static final short NOT_SUSTAINS_LEAVES = -1;
	private static final short IS_LEAVES = -2;
	private static int[] leafDecayValues;

	public static void leafDecay(BlockLeavesBase leaves, World world, BlockPos pos) {
		if (world.isRemote) {
			return;
		}

		IBlockState state = world.getBlockState(pos);
		int leafMeta = state.getBlock().getMetaFromState(state);

		if ((leafMeta & 8) != 0 && (leafMeta & 4) == 0) {
			byte radius = 4;
			byte yArrayMult = 32;
			int xArrayMult = yArrayMult * yArrayMult;
			int arrayOffset = yArrayMult / 2;

			if (leafDecayValues == null) {
				leafDecayValues = new int[yArrayMult * yArrayMult * yArrayMult];
			}

			int radius1 = radius + 1;
			if (BlockUtil.checkChunksExist(world, pos.getX() - radius1, pos.getY() - radius1, pos.getZ() - radius1, pos.getX() + radius1, pos.getY() + radius1, pos.getZ() + radius1)) {
				for (int xOffset = -radius; xOffset <= radius; ++xOffset) {
					for (int yOffset = -radius; yOffset <= radius; ++yOffset) {
						for (int zOffset = -radius; zOffset <= radius; ++zOffset) {
							BlockPos posO = new BlockPos(pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset);
							IBlockState stateO = world.getBlockState(posO);
							Block block = stateO.getBlock();

							if (!block.canSustainLeaves(world, pos)) {
								if (block.isLeaves(world, posO)) {
									leafDecayValues[(xOffset + arrayOffset) * xArrayMult + (yOffset + arrayOffset) * yArrayMult + zOffset + arrayOffset] = IS_LEAVES;
								} else {
									leafDecayValues[(xOffset + arrayOffset) * xArrayMult + (yOffset + arrayOffset) * yArrayMult + zOffset + arrayOffset] = NOT_SUSTAINS_LEAVES;
								}
							} else {
								leafDecayValues[(xOffset + arrayOffset) * xArrayMult + (yOffset + arrayOffset) * yArrayMult + zOffset + arrayOffset] = SUSTAINS_LEAVES;
							}
						}
					}
				}

				for (int sustainedValue = 1; sustainedValue <= 8; ++sustainedValue) {
					for (int xOffset = -radius; xOffset <= radius; ++xOffset) {
						for (int yOffset = -radius; yOffset <= radius; ++yOffset) {
							for (int zOffset = -radius; zOffset <= radius; ++zOffset) {
								if (leafDecayValues[(xOffset + arrayOffset) * xArrayMult + (yOffset + arrayOffset) * yArrayMult + zOffset + arrayOffset] == sustainedValue - 1) {
									if (leafDecayValues[(xOffset + arrayOffset - 1) * xArrayMult + (yOffset + arrayOffset) * yArrayMult + zOffset + arrayOffset] == IS_LEAVES) {
										leafDecayValues[(xOffset + arrayOffset - 1) * xArrayMult + (yOffset + arrayOffset) * yArrayMult + zOffset + arrayOffset] = sustainedValue;
									}

									if (leafDecayValues[(xOffset + arrayOffset + 1) * xArrayMult + (yOffset + arrayOffset) * yArrayMult + zOffset + arrayOffset] == IS_LEAVES) {
										leafDecayValues[(xOffset + arrayOffset + 1) * xArrayMult + (yOffset + arrayOffset) * yArrayMult + zOffset + arrayOffset] = sustainedValue;
									}

									if (leafDecayValues[(xOffset + arrayOffset) * xArrayMult + (yOffset + arrayOffset - 1) * yArrayMult + zOffset + arrayOffset] == IS_LEAVES) {
										leafDecayValues[(xOffset + arrayOffset) * xArrayMult + (yOffset + arrayOffset - 1) * yArrayMult + zOffset + arrayOffset] = sustainedValue;
									}

									if (leafDecayValues[(xOffset + arrayOffset) * xArrayMult + (yOffset + arrayOffset + 1) * yArrayMult + zOffset + arrayOffset] == IS_LEAVES) {
										leafDecayValues[(xOffset + arrayOffset) * xArrayMult + (yOffset + arrayOffset + 1) * yArrayMult + zOffset + arrayOffset] = sustainedValue;
									}

									if (leafDecayValues[(xOffset + arrayOffset) * xArrayMult + (yOffset + arrayOffset) * yArrayMult + (zOffset + arrayOffset - 1)] == IS_LEAVES) {
										leafDecayValues[(xOffset + arrayOffset) * xArrayMult + (yOffset + arrayOffset) * yArrayMult + (zOffset + arrayOffset - 1)] = sustainedValue;
									}

									if (leafDecayValues[(xOffset + arrayOffset) * xArrayMult + (yOffset + arrayOffset) * yArrayMult + zOffset + arrayOffset + 1] == IS_LEAVES) {
										leafDecayValues[(xOffset + arrayOffset) * xArrayMult + (yOffset + arrayOffset) * yArrayMult + zOffset + arrayOffset + 1] = sustainedValue;
									}
								}
							}
						}
					}
				}
			}

			int sustainValue = leafDecayValues[(arrayOffset * xArrayMult) + (arrayOffset * yArrayMult) + arrayOffset];

			if (sustainValue >= 0) {
				world.setBlockState(pos, state.getBlock().getStateFromMeta(leafMeta & -9), 4); // stop trying to decay
			} else {
				leaves.dropBlockAsItem(world, pos, state, 0);
				world.setBlockToAir(pos);
			}
		}
	}
}
