package forestry.core.worldgen;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import forestry.api.world.ITreeGenData;
import forestry.arboriculture.worldgen.ITreeBlockType;
import forestry.arboriculture.worldgen.TreeBlockType;
import forestry.core.utils.VectUtil;

public class WorldGenHelper {

	public static boolean addBlock(World world, BlockPos pos, ITreeBlockType type, EnumReplaceMode replaceMode) {
		if (!world.isBlockLoaded(pos)) {
			return false;
		}

		IBlockState blockState = world.getBlockState(pos);
		if (replaceMode.canReplace(blockState, world, pos)) {
			type.setBlock(world, pos);
			return true;
		}

		return false;
	}

	/**
	 * Uses centerPos and girth of a tree to calculate the center
	 */
	public static void generateCylinderFromTreeStartPos(World world, ITreeBlockType block, BlockPos startPos, int girth, float radius, int height, EnumReplaceMode replace) {
		generateCylinderFromPos(world, block, startPos.add(girth / 2, 0, girth / 2), radius, height, replace);
	}

	/**
	 * Center is the bottom middle of the cylinder
	 */
	public static void generateCylinderFromPos(World world, ITreeBlockType block, BlockPos center, float radius, int height, EnumReplaceMode replace) {
		BlockPos start = new BlockPos(center.getX() - radius, center.getY(), center.getZ() - radius);
		for (int x = 0; x < radius * 2 + 1; x++) {
			for (int y = height - 1; y >= 0; y--) { // generating top-down is faster for lighting calculations
				for (int z = 0; z < radius * 2 + 1; z++) {
					BlockPos position = start.add(x, y, z);
					Vec3i treeCenter = new Vec3i(center.getX(), position.getY(), center.getZ());
					if (position.distanceSq(treeCenter) <= radius * radius + 0.01) {
						EnumFacing direction = VectUtil.direction(position, treeCenter);
						block.setDirection(direction);
						addBlock(world, position, block, replace);
					}
				}
			}
		}
	}

	public static void generateCircleFromTreeStartPos(World world, Random rand, BlockPos startPos, int girth, float radius, int width, int height, ITreeBlockType block, float chance, EnumReplaceMode replace) {
		generateCircle(world, rand, startPos.add(girth / 2, 0, girth / 2), radius, width, height, block, chance, replace);
	}

	public static void generateCircle(World world, Random rand, BlockPos center, float radius, int width, int height, ITreeBlockType block, float chance, EnumReplaceMode replace) {
		Vec3i start = new Vec3i(center.getX() - radius, center.getY(), center.getZ() - radius);
		Vec3i area = new Vec3i(radius * 2 + 1, height, radius * 2 + 1);

		for (int x = start.getX(); x < start.getX() + area.getX(); x++) {
			for (int y = start.getY() + area.getY() - 1; y >= start.getY(); y--) { // generating top-down is faster for lighting calculations
				for (int z = start.getZ(); z < start.getZ() + area.getZ(); z++) {

					if (rand.nextFloat() > chance) {
						continue;
					}

					double distance = new Vec3i(x, y, z).getDistance(center.getX(), y, center.getZ());
					if (radius - width - 0.01 < distance && distance <= radius + 0.01) {
						addBlock(world, new BlockPos(x, y, z), block, replace);
					}
				}
			}
		}
	}

	public static void generateSphereFromTreeStartPos(World world, BlockPos startPos, int girth, int radius, ITreeBlockType block, EnumReplaceMode replace) {
		generateSphere(world, startPos.add(girth / 2, 0, girth / 2), radius, block, replace);
	}

	public static void generateSphere(World world, BlockPos center, int radius, ITreeBlockType block, EnumReplaceMode replace) {
		Vec3i start = new Vec3i(center.getX() - radius, center.getY() - radius, center.getZ() - radius);
		Vec3i area = new Vec3i(radius * 2 + 1, radius * 2 + 1, radius * 2 + 1);
		for (int x = start.getX(); x < start.getX() + area.getX(); x++) {
			for (int y = start.getY() + area.getY() - 1; y >= start.getY(); y--) { // generating top-down is faster for lighting calculations
				for (int z = start.getZ(); z < start.getZ() + area.getZ(); z++) {
					if (center.getDistance(x, y, z) <= radius + 0.01) {
						addBlock(world, new BlockPos(x, y, z), block, replace);
					}
				}
			}
		}
	}

	/**
	 * Returns a list of trunk top coordinates
	 */
	public static Set<BlockPos> generateTreeTrunk(
		World world,
		Random rand,
		ITreeBlockType wood,
		BlockPos startPos,
		int height,
		int girth,
		int yStart,
		float vinesChance,
		@Nullable EnumFacing leanDirection,
		float leanScale
	) {
		Set<BlockPos> treeTops = new HashSet<>();

		final int leanStartY = (int) Math.floor(height * 0.33f);
		int prevXOffset = 0;
		int prevZOffset = 0;

		int leanX = 0;
		int leanZ = 0;

		if (leanDirection != null) {
			leanX = leanDirection.getXOffset();
			leanZ = leanDirection.getZOffset();
		}

		for (int x = 0; x < girth; x++) {
			for (int z = 0; z < girth; z++) {
				for (int y = height - 1; y >= yStart; y--) { // generating top-down is faster for lighting calculations
					float lean;
					if (y < leanStartY) {
						lean = 0;
					} else {
						lean = leanScale * (y - leanStartY) / (height - leanStartY);
					}
					int xOffset = (int) Math.floor(leanX * lean);
					int zOffset = (int) Math.floor(leanZ * lean);

					if (xOffset != prevXOffset || zOffset != prevZOffset) {
						prevXOffset = xOffset;
						prevZOffset = zOffset;
						if (y > 0) {
							if (leanDirection != null) {
								wood.setDirection(leanDirection);
							}
							addBlock(world, startPos.add(x + xOffset, y - 1, z + zOffset), wood, EnumReplaceMode.ALL);
							wood.setDirection(EnumFacing.UP);
						}
					}

					BlockPos pos = startPos.add(x + xOffset, y, z + zOffset);
					addBlock(world, pos, wood, EnumReplaceMode.ALL);
					addVines(world, rand, pos, vinesChance);

					if (y + 1 == height) {
						treeTops.add(pos);
					}
				}
			}
		}

		return treeTops;
	}

	protected static void addVines(World world, Random rand, BlockPos pos, float chance) {
		if (chance <= 0) {
			return;
		}

		if (rand.nextFloat() < chance) {
			IBlockState blockState = Blocks.VINE.getDefaultState().withProperty(BlockVine.EAST, true);
			addBlock(world, pos.west(), new TreeBlockType(blockState), EnumReplaceMode.AIR);
		}
		if (rand.nextFloat() < chance) {
			IBlockState blockState = Blocks.VINE.getDefaultState().withProperty(BlockVine.WEST, true);
			addBlock(world, pos.east(), new TreeBlockType(blockState), EnumReplaceMode.AIR);
		}
		if (rand.nextFloat() < chance) {
			IBlockState blockState = Blocks.VINE.getDefaultState().withProperty(BlockVine.SOUTH, true);
			addBlock(world, pos.north(), new TreeBlockType(blockState), EnumReplaceMode.AIR);
		}
		if (rand.nextFloat() < chance) {
			IBlockState blockState = Blocks.VINE.getDefaultState().withProperty(BlockVine.NORTH, true);
			addBlock(world, pos.south(), new TreeBlockType(blockState), EnumReplaceMode.AIR);
		}
	}

	public static void generatePods(ITreeGenData tree, World world, Random rand, BlockPos startPos, int height, int minHeight, int girth, EnumReplaceMode replaceMode) {
		for (int y = height - 1; y >= minHeight; y--) { // generating top-down is faster for lighting calculations
			for (int x = 0; x < girth; x++) {
				for (int z = 0; z < girth; z++) {

					if (x > 0 && x < girth && z > 0 && z < girth) {
						continue;
					}

					trySpawnFruitBlock(tree, world, rand, startPos.add(x + 1, y, z), replaceMode);
					trySpawnFruitBlock(tree, world, rand, startPos.add(x - 1, y, z), replaceMode);
					trySpawnFruitBlock(tree, world, rand, startPos.add(x, y, z + 1), replaceMode);
					trySpawnFruitBlock(tree, world, rand, startPos.add(x, y, z - 1), replaceMode);
				}
			}
		}
	}

	private static void trySpawnFruitBlock(ITreeGenData tree, World world, Random rand, BlockPos pos, EnumReplaceMode replaceMode) {
		IBlockState blockState = world.getBlockState(pos);
		if (replaceMode.canReplace(blockState, world, pos)) {
			tree.trySpawnFruitBlock(world, rand, pos);
		}
	}

	public static void generateSupportStems(ITreeBlockType wood, World world, Random rand, BlockPos startPos, int height, int girth, float chance, float maxHeight) {

		final int min = -1;
		final int max = girth;

		for (int x = min; x <= max; x++) {
			for (int z = min; z <= max; z++) {

				// skip the corners, support stems should touch the body of the trunk
				if ((x == min && z == min) || (x == max && z == max) || (x == min && z == max) || (x == max && z == min)) {
					continue;
				}

				int stemHeight = rand.nextInt(Math.round(height * maxHeight));
				if (rand.nextFloat() < chance) {
					for (int y = 0; y < stemHeight; y++) {
						addBlock(world, startPos.add(x, y, z), wood, EnumReplaceMode.SOFT);
					}
				}
			}
		}
	}

	public static Set<BlockPos> generateBranches(final World world, final Random rand, final ITreeBlockType wood, final BlockPos startPos, final int girth, final float spreadY, final float spreadXZ, int radius, final int count, final float chance) {
		Set<BlockPos> branchEnds = new HashSet<>();
		if (radius < 1) {
			radius = 1;
		}

		for (final EnumFacing branchDirection : EnumFacing.HORIZONTALS) {
			wood.setDirection(branchDirection);

			BlockPos branchStart = startPos;

			int offsetX = branchDirection.getXOffset();
			int offsetZ = branchDirection.getZOffset();
			if (offsetX > 0) {
				branchStart = branchStart.add(girth - 1, 0, 0);
			}
			if (offsetZ > 0) {
				branchStart = branchStart.add(0, 0, girth - 1);
			}

			for (int i = 0; i < count; i++) {
				if (rand.nextFloat() > chance) {
					continue;
				}
				int y = 0;
				int x = 0;
				int z = 0;

				BlockPos branchEnd = null;
				for (int r = 0; r < radius; r++) {
					if (rand.nextFloat() < spreadY) {
						// make branches only spread up, not down
						y++;
						wood.setDirection(EnumFacing.UP);
					} else {
						if (rand.nextFloat() < spreadXZ) {
							if (branchDirection.getAxis() == EnumFacing.Axis.Z) {
								if (rand.nextBoolean()) {
									x++;
								} else {
									x--;
								}
								wood.setDirection(EnumFacing.EAST);
							} else if (branchDirection.getAxis() == EnumFacing.Axis.X) {
								if (rand.nextBoolean()) {
									z++;
								} else {
									z--;
								}
								wood.setDirection(EnumFacing.SOUTH);
							}
						} else {
							x += offsetX;
							z += offsetZ;
							wood.setDirection(branchDirection);
						}
					}

					BlockPos pos = branchStart.add(x, y, z);
					if (addBlock(world, pos, wood, EnumReplaceMode.SOFT)) {
						branchEnd = pos;
					} else {
						break;
					}
				}

				if (branchEnd != null) {
					branchEnds.add(branchEnd);
				}
			}
		}

		return branchEnds;
	}

	public enum EnumReplaceMode {
		AIR {
			@Override
			public boolean canReplace(IBlockState blockState, World world, BlockPos pos) {
				return world.isAirBlock(pos);
			}
		},
		ALL {
			@Override
			public boolean canReplace(IBlockState blockState, World world, BlockPos pos) {
				return true;
			}
		},
		SOFT {
			@Override
			public boolean canReplace(IBlockState blockState, World world, BlockPos pos) {
				return blockState.getBlock().isReplaceable(world, pos);
			}
		};

		public abstract boolean canReplace(IBlockState blockState, World world, BlockPos pos);
	}

	public enum Direction {
		NORTH(EnumFacing.NORTH),
		SOUTH(EnumFacing.SOUTH),
		WEST(EnumFacing.WEST),
		EAST(EnumFacing.EAST);

		public final EnumFacing facing;

		Direction(EnumFacing forgeDirection) {
			this.facing = forgeDirection;
		}

		public static Direction getRandom(Random random) {
			return values()[random.nextInt(values().length)];
		}

		public static Direction getRandomOther(Random random, Direction direction) {
			EnumSet<Direction> directions = EnumSet.allOf(Direction.class);
			directions.remove(direction);
			int size = directions.size();
			return directions.toArray(new Direction[size])[random.nextInt(size)];
		}
	}
}
