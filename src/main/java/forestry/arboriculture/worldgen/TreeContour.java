package forestry.arboriculture.worldgen;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/**
 * Helper class for storing data that is relevant to generating trees
 */
public interface TreeContour {
	TreeContour EMPTY = new Empty();

	default void addLeaf(BlockPos pos) {
	}

	default Collection<BlockPos> getBranchEnds() {
		return Collections.emptyList();
	}

	class Impl implements TreeContour {
		public final Set<BlockPos> leavePositions;
		public final List<BlockPos> branchEnds;
		public final BoundingBox boundingBox;

		public Impl(List<BlockPos> branchEnds) {
			this.leavePositions = new HashSet<>();
			this.branchEnds = branchEnds;
			this.boundingBox = BoundingBox.infinite(); // BoundingBox.getUnknownBox();
		}

		@Override
		public void addLeaf(BlockPos pos) {
			leavePositions.add(pos.immutable());
			// boundingBox.expand(new BoundingBox(pos, pos));
		}

		@Override
		public List<BlockPos> getBranchEnds() {
			return branchEnds;
		}
	}

	class Empty implements TreeContour {
		private Empty() {
		}
	}
}


