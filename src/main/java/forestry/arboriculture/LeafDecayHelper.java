package forestry.arboriculture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/** Based on vanilla leaf decay, but accepts leaves that are connected further from a trunk */
public abstract class LeafDecayHelper {
	private static final short SUSTAINS_LEAVES = 0;
	private static final short NOT_SUSTAINS_LEAVES = -1;
	private static final short IS_LEAVES = -2;
	private static int[] leafDecayValues;

	public static void leafDecay(BlockLeaves leaves, World world, int x, int y, int z) {
		if (world.isRemote) {
			return;
		}

		IBlockState state = world.getBlockState(new BlockPos(x, y, z));
		int leafMeta = leaves.getMetaFromState(state);

		if ((leafMeta & 8) != 0 && (leafMeta & 4) == 0) {
			byte radius = 4;
			byte yArrayMult = 32;
			int xArrayMult = yArrayMult * yArrayMult;
			int arrayOffset = yArrayMult / 2;

			if (leafDecayValues == null) {
				leafDecayValues = new int[yArrayMult * yArrayMult * yArrayMult];
			}

			int radius1 = radius + 1;
			if (world.isAreaLoaded(new BlockPos(x - radius1, y - radius1, z - radius1), new BlockPos(x + radius1, y + radius1, z + radius1), false)) {
				for (int xOffset = -radius; xOffset <= radius; ++xOffset) {
					for (int yOffset = -radius; yOffset <= radius; ++yOffset) {
						for (int zOffset = -radius; zOffset <= radius; ++zOffset) {
							Block block = world.getBlockState(new BlockPos(x + xOffset, y + yOffset, z + zOffset)).getBlock();

							if (!block.canSustainLeaves(world, new BlockPos(x + xOffset, y + yOffset, z + zOffset))) {
								if (block.isLeaves(world, new BlockPos(x + xOffset, y + yOffset, z + zOffset))) {
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
				BlockPos pos = new BlockPos(x, y, z);
				IBlockState stateNew = world.getBlockState(pos);
				world.setBlockState(pos, stateNew.getBlock().getStateFromMeta(leafMeta & -9), 4); // stop trying to decay
			} else {
				BlockPos pos = new BlockPos(x, y, z);
				IBlockState stateNew = world.getBlockState(pos);
				leaves.dropBlockAsItem(world, pos, stateNew, 0);
				world.setBlockToAir(pos);
			}
		}
	}
}
