package forestry.core.multiblock.rectangular;

import forestry.core.multiblock.CoordTriplet;
import forestry.core.multiblock.MultiblockControllerBase;
import forestry.core.multiblock.MultiblockTileEntityBase;
import forestry.core.multiblock.MultiblockValidationException;
import net.minecraft.util.EnumFacing;

public abstract class RectangularMultiblockTileEntityBase extends MultiblockTileEntityBase {

	PartPosition position;
	EnumFacing outwards;

	public RectangularMultiblockTileEntityBase() {
		super();

		position = PartPosition.Unknown;
		outwards = null;
	}

	// Positional Data
	public EnumFacing getOutwardsDir() {
		return outwards;
	}

	public PartPosition getPartPosition() {
		return position;
	}

	// Handlers from MultiblockTileEntityBase
	@Override
	public void onAttached(MultiblockControllerBase newController) {
		super.onAttached(newController);
		recalculateOutwardsDirection(newController.getMinimumCoord(), newController.getMaximumCoord());
	}

	@Override
	public void onMachineAssembled(MultiblockControllerBase controller) {
		CoordTriplet maxCoord = controller.getMaximumCoord();
		CoordTriplet minCoord = controller.getMinimumCoord();

		// Discover where I am on the reactor
		recalculateOutwardsDirection(minCoord, maxCoord);
	}

	@Override
	public void onMachineBroken() {
		position = PartPosition.Unknown;
		outwards = null;
	}

	// Positional helpers
	public void recalculateOutwardsDirection(CoordTriplet minCoord, CoordTriplet maxCoord) {
		outwards = null;
		position = PartPosition.Unknown;

		int facesMatching = 0;
		if (maxCoord.pos.getX() == pos.getX() || minCoord.pos.getX() == pos.getX()) {
			facesMatching++;
		}
		if (maxCoord.pos.getY() == pos.getY() || minCoord.pos.getY() == pos.getY()) {
			facesMatching++;
		}
		if (maxCoord.pos.getZ() == pos.getZ() || minCoord.pos.getZ() == pos.getZ()) {
			facesMatching++;
		}

		if (facesMatching <= 0) {
			position = PartPosition.Interior;
		} else if (facesMatching >= 3) {
			position = PartPosition.FrameCorner;
		} else if (facesMatching == 2) {
			position = PartPosition.Frame;
		} else {
			// 1 face matches
			if (maxCoord.pos.getX() == pos.getX()) {
				position = PartPosition.EastFace;
				outwards = EnumFacing.EAST;
			} else if (minCoord.pos.getX() == pos.getX()) {
				position = PartPosition.WestFace;
				outwards = EnumFacing.WEST;
			} else if (maxCoord.pos.getZ() == pos.getZ()) {
				position = PartPosition.SouthFace;
				outwards = EnumFacing.SOUTH;
			} else if (minCoord.pos.getZ() == pos.getZ()) {
				position = PartPosition.NorthFace;
				outwards = EnumFacing.NORTH;
			} else if (maxCoord.pos.getY() == pos.getY()) {
				position = PartPosition.TopFace;
				outwards = EnumFacing.UP;
			} else {
				position = PartPosition.BottomFace;
				outwards = EnumFacing.DOWN;
			}
		}
	}

	///// Validation Helpers (IMultiblockPart)

	public abstract void isGoodForExteriorLevel(int level) throws MultiblockValidationException;

	public abstract void isGoodForInterior() throws MultiblockValidationException;
}
