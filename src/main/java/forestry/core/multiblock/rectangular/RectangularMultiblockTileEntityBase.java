package forestry.core.multiblock.rectangular;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.core.multiblock.CoordTriplet;
import forestry.core.multiblock.MultiblockControllerBase;
import forestry.core.multiblock.MultiblockTileEntityBase;
import forestry.core.multiblock.MultiblockValidationException;

public abstract class RectangularMultiblockTileEntityBase extends MultiblockTileEntityBase {

	PartPosition position;
	ForgeDirection outwards;
	
	public RectangularMultiblockTileEntityBase() {
		super();
		
		position = PartPosition.Unknown;
		outwards = ForgeDirection.UNKNOWN;
	}

	// Positional Data
	public ForgeDirection getOutwardsDir() {
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
		outwards = ForgeDirection.UNKNOWN;
	}
	
	// Positional helpers
	public void recalculateOutwardsDirection(CoordTriplet minCoord, CoordTriplet maxCoord) {
		outwards = ForgeDirection.UNKNOWN;
		position = PartPosition.Unknown;

		int facesMatching = 0;
		if (maxCoord.x == this.xCoord || minCoord.x == this.xCoord) {
			facesMatching++;
		}
		if (maxCoord.y == this.yCoord || minCoord.y == this.yCoord) {
			facesMatching++;
		}
		if (maxCoord.z == this.zCoord || minCoord.z == this.zCoord) {
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
			if (maxCoord.x == this.xCoord) {
				position = PartPosition.EastFace;
				outwards = ForgeDirection.EAST;
			} else if (minCoord.x == this.xCoord) {
				position = PartPosition.WestFace;
				outwards = ForgeDirection.WEST;
			} else if (maxCoord.z == this.zCoord) {
				position = PartPosition.SouthFace;
				outwards = ForgeDirection.SOUTH;
			} else if (minCoord.z == this.zCoord) {
				position = PartPosition.NorthFace;
				outwards = ForgeDirection.NORTH;
			} else if (maxCoord.y == this.yCoord) {
				position = PartPosition.TopFace;
				outwards = ForgeDirection.UP;
			} else {
				position = PartPosition.BottomFace;
				outwards = ForgeDirection.DOWN;
			}
		}
	}
	
	///// Validation Helpers (IMultiblockPart)

	public abstract void isGoodForExteriorLevel(int level) throws MultiblockValidationException;

	public abstract void isGoodForInterior() throws MultiblockValidationException;
}
