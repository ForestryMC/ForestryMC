package forestry.arboriculture.genetics;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;

import genetics.api.individual.IGenome;

import forestry.api.arboriculture.genetics.ITree;
import forestry.arboriculture.tiles.TileSapling;
import forestry.core.tiles.TileUtil;

public class TreeGrowthHelper {
	@Nullable
	public static BlockPos canGrow(IWorld world, IGenome genome, BlockPos pos, int expectedGirth, int expectedHeight) {
		BlockPos growthPos = hasSufficientSaplingsAroundSapling(genome, world, pos, expectedGirth);
		if (growthPos == null) {
			return null;
		}

		if (!hasRoom(world, growthPos, expectedGirth, expectedHeight)) {
			return null;
		}

		return growthPos;
	}

	private static boolean hasRoom(IWorld world, BlockPos pos, int expectedGirth, int expectedHeight) {
		Vec3i area = new Vec3i(expectedGirth, expectedHeight + 1, expectedGirth);
		return checkArea(world, pos.up(), area);
	}

	private static boolean checkArea(IWorld world, BlockPos start, Vec3i area) {
		for (int x = 0; x < area.getX(); x++) {
			for (int y = 0; y < area.getY(); y++) {
				for (int z = 0; z < area.getZ(); z++) {
					BlockPos pos = start.add(x, y, z);
					BlockState blockState = world.getBlockState(pos);
					//TODO: Can't be used because the world generation only provides a IWorld and not a World
					/*BlockItemUseContext context = new DirectionalPlaceContext((World) world, pos, Direction.DOWN, ItemStack.EMPTY, Direction.UP);
					return blockState.isReplaceable(context);*/
					if (!blockState.getMaterial().isReplaceable()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Checks an area for saplings.
	 * If saplings need to be 2x2, 3x3, etc, it will check all configurations in which pos is included in that area.
	 * Uses a knownSaplings cache to avoid checking the same saplings multiple times.
	 */
	@Nullable
	private static BlockPos hasSufficientSaplingsAroundSapling(IGenome genome, IWorld world, BlockPos saplingPos, int expectedGirth) {
		final int checkSize = (expectedGirth * 2) - 1;
		final int offset = expectedGirth - 1;
		final Map<BlockPos, Boolean> knownSaplings = new HashMap<>(checkSize * checkSize);

		for (int x = -offset; x <= 0; x++) {
			for (int z = -offset; z <= 0; z++) {
				BlockPos startPos = saplingPos.add(x, 0, z);
				if (checkForSaplings(genome, world, startPos, expectedGirth, knownSaplings)) {
					return startPos;
				}
			}
		}

		return null;
	}

	private static boolean checkForSaplings(IGenome genome, IWorld world, BlockPos startPos, int girth, Map<BlockPos, Boolean> knownSaplings) {
		for (int x = 0; x < girth; x++) {
			for (int z = 0; z < girth; z++) {
				BlockPos checkPos = startPos.add(x, 0, z);
				Boolean knownSapling = knownSaplings.computeIfAbsent(checkPos, k -> isSapling(genome, world, checkPos));
				if (!knownSapling) {
					return false;
				}
			}
		}
		return true;
	}

	private static boolean isSapling(IGenome genome, IWorld world, BlockPos pos) {
		if (!world.isBlockLoaded(pos)) {
			return false;
		}

		if (world.isAirBlock(pos)) {
			return false;
		}

		TileSapling sapling = TileUtil.getTile(world, pos, TileSapling.class);
		if (sapling == null) {
			return false;
		}

		ITree tree = sapling.getTree();
		return tree != null && tree.getGenome().getPrimary().getRegistryName().equals(genome.getPrimary().getRegistryName());
	}
}
