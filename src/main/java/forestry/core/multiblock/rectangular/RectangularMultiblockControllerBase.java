package forestry.core.multiblock.rectangular;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import forestry.core.multiblock.CoordTriplet;
import forestry.core.multiblock.MultiblockControllerBase;
import forestry.core.multiblock.MultiblockValidationException;

public abstract class RectangularMultiblockControllerBase extends MultiblockControllerBase {

	protected RectangularMultiblockControllerBase(World world) {
		super(world);
	}

	@Override
	protected void isMachineWhole() throws MultiblockValidationException {
		int minX = getMinimumXSize();
		int minY = getMinimumYSize();
		int minZ = getMinimumZSize();

		if (connectedParts.size() < getMinimumNumberOfBlocksForAssembledMachine()) {
			throw new MultiblockValidationException(StatCollector.translateToLocalFormatted("for.multiblock.error.small", minX, minY, minZ));
		}
		
		CoordTriplet maximumCoord = getMaximumCoord();
		CoordTriplet minimumCoord = getMinimumCoord();
		
		// Quickly check for exceeded dimensions
		int deltaX = maximumCoord.x - minimumCoord.x + 1;
		int deltaY = maximumCoord.y - minimumCoord.y + 1;
		int deltaZ = maximumCoord.z - minimumCoord.z + 1;
		
		int maxX = getMaximumXSize();
		int maxY = getMaximumYSize();
		int maxZ = getMaximumZSize();

		if (maxX > 0 && deltaX > maxX) {
			throw new MultiblockValidationException(StatCollector.translateToLocalFormatted("for.multiblock.error.large.x", maxX));
		}
		if (maxY > 0 && deltaY > maxY) {
			throw new MultiblockValidationException(StatCollector.translateToLocalFormatted("for.multiblock.error.large.y", maxY));
		}
		if (maxZ > 0 && deltaZ > maxZ) {
			throw new MultiblockValidationException(StatCollector.translateToLocalFormatted("for.multiblock.error.large.z", maxZ));
		}
		if (deltaX < minX) {
			throw new MultiblockValidationException(StatCollector.translateToLocalFormatted("for.multiblock.error.small.x", minX));
		}
		if (deltaY < minY) {
			throw new MultiblockValidationException(StatCollector.translateToLocalFormatted("for.multiblock.error.small.y", minY));
		}
		if (deltaZ < minZ) {
			throw new MultiblockValidationException(StatCollector.translateToLocalFormatted("for.multiblock.error.small.z", minZ));
		}

		// Now we run a simple check on each block within that volume.
		// Any block deviating = NO DEAL SIR
		TileEntity te;
		RectangularMultiblockTileEntityBase part;
		Class<? extends RectangularMultiblockControllerBase> myClass = this.getClass();

		for (int x = minimumCoord.x; x <= maximumCoord.x; x++) {
			for (int y = minimumCoord.y; y <= maximumCoord.y; y++) {
				for (int z = minimumCoord.z; z <= maximumCoord.z; z++) {
					// Okay, figure out what sort of block this should be.
					
					te = this.worldObj.getTileEntity(x, y, z);
					if (te instanceof RectangularMultiblockTileEntityBase) {
						part = (RectangularMultiblockTileEntityBase) te;
						
						// Ensure this part should actually be allowed within a cube of this controller's type
						if (!myClass.equals(part.getMultiblockControllerType())) {
							throw new MultiblockValidationException(StatCollector.translateToLocalFormatted("for.multiblock.error.invalid.part", x, y, z, myClass.getSimpleName()));
						}
					} else {
						// This is permitted so that we can incorporate certain non-multiblock parts inside interiors
						part = null;
					}
					
					// Validate block type against both part-level and material-level validators.
					int extremes = 0;
					if (x == minimumCoord.x) {
						extremes++;
					}
					if (y == minimumCoord.y) {
						extremes++;
					}
					if (z == minimumCoord.z) {
						extremes++;
					}
					
					if (x == maximumCoord.x) {
						extremes++;
					}
					if (y == maximumCoord.y) {
						extremes++;
					}
					if (z == maximumCoord.z) {
						extremes++;
					}
					
					if (extremes >= 1) {
						// Side
						int exteriorLevel = y - minimumCoord.y;
						if (part != null) {
							part.isGoodForExteriorLevel(exteriorLevel);
						} else {
							isBlockGoodForExteriorLevel(exteriorLevel, this.worldObj, x, y, z);
						}
					} else {
						if (part != null) {
							part.isGoodForInterior();
						} else {
							isBlockGoodForInterior(this.worldObj, x, y, z);
						}
					}
				}
			}
		}
	}
	
}
