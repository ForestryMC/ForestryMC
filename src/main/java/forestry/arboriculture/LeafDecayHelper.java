package forestry.arboriculture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
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

		int leafMeta = world.getBlockMetadata(x, y, z);

		if ((leafMeta & 8) != 0 && (leafMeta & 4) == 0) {
			byte radius = 4;
			byte yArrayMult = 32;
			int xArrayMult = yArrayMult * yArrayMult;
			int arrayOffset = yArrayMult / 2;

			if (leafDecayValues == null) {
				leafDecayValues = new int[yArrayMult * yArrayMult * yArrayMult];
			}

			int radius1 = radius + 1;
			if (world.checkChunksExist(x - radius1, y - radius1, z - radius1, x + radius1, y + radius1, z + radius1)) {
				for (int xOffset = -radius; xOffset <= radius; ++xOffset) {
					for (int yOffset = -radius; yOffset <= radius; ++yOffset) {
						for (int zOffset = -radius; zOffset <= radius; ++zOffset) {
							Block block = world.getBlock(x + xOffset, y + yOffset, z + zOffset);

							if (!block.canSustainLeaves(world, x + xOffset, y + yOffset, z + zOffset)) {
								if (block.isLeaves(world, x + xOffset, y + yOffset, z + zOffset)) {
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
				world.setBlockMetadataWithNotify(x, y, z, leafMeta & -9, 4); // stop trying to decay
			} else {
				leaves.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
				world.setBlockToAir(x, y, z);
			}
		}
	}
}
