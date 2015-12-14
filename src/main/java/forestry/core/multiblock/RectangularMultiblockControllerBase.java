package forestry.core.multiblock;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import forestry.api.multiblock.IMultiblockComponent;

public abstract class RectangularMultiblockControllerBase extends MultiblockControllerForestry {

	private final IMultiblockSizeLimits sizeLimits;

	protected RectangularMultiblockControllerBase(World world, IMultiblockSizeLimits sizeLimits) {
		super(world);
		this.sizeLimits = sizeLimits;
	}

	@Override
	protected void isMachineWhole() throws MultiblockValidationException {
		int minX = sizeLimits.getMinimumXSize();
		int minY = sizeLimits.getMinimumYSize();
		int minZ = sizeLimits.getMinimumZSize();

		if (connectedParts.size() < sizeLimits.getMinimumNumberOfBlocksForAssembledMachine()) {
			throw new MultiblockValidationException(StatCollector.translateToLocalFormatted("for.multiblock.error.small", minX, minY, minZ));
		}
		
		ChunkCoordinates maximumCoord = getMaximumCoord();
		ChunkCoordinates minimumCoord = getMinimumCoord();
		
		// Quickly check for exceeded dimensions
		int deltaX = maximumCoord.posX - minimumCoord.posX + 1;
		int deltaY = maximumCoord.posY - minimumCoord.posY + 1;
		int deltaZ = maximumCoord.posZ - minimumCoord.posZ + 1;
		
		int maxX = sizeLimits.getMaximumXSize();
		int maxY = sizeLimits.getMaximumYSize();
		int maxZ = sizeLimits.getMaximumZSize();

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
		IMultiblockComponent part;
		Class<? extends RectangularMultiblockControllerBase> myClass = this.getClass();

		for (int x = minimumCoord.posX; x <= maximumCoord.posX; x++) {
			for (int y = minimumCoord.posY; y <= maximumCoord.posY; y++) {
				for (int z = minimumCoord.posZ; z <= maximumCoord.posZ; z++) {
					// Okay, figure out what sort of block this should be.
					
					te = this.worldObj.getTileEntity(x, y, z);
					if (te instanceof IMultiblockComponent) {
						part = (IMultiblockComponent) te;
						
						// Ensure this part should actually be allowed within a cube of this controller's type
						if (!myClass.equals(part.getMultiblockLogic().getController().getClass())) {
							throw new MultiblockValidationException(StatCollector.translateToLocalFormatted("for.multiblock.error.invalid.part", x, y, z, myClass.getSimpleName()));
						}
					} else {
						// This is permitted so that we can incorporate certain non-multiblock parts inside interiors
						part = null;
					}
					
					// Validate block type against both part-level and material-level validators.
					int extremes = 0;
					if (x == minimumCoord.posX) {
						extremes++;
					}
					if (y == minimumCoord.posY) {
						extremes++;
					}
					if (z == minimumCoord.posZ) {
						extremes++;
					}
					
					if (x == maximumCoord.posX) {
						extremes++;
					}
					if (y == maximumCoord.posY) {
						extremes++;
					}
					if (z == maximumCoord.posZ) {
						extremes++;
					}
					
					if (extremes >= 1) {
						// Side
						int exteriorLevel = y - minimumCoord.posY;
						if (part != null) {
							isGoodForExteriorLevel(part, exteriorLevel);
						} else {
							isBlockGoodForExteriorLevel(exteriorLevel, this.worldObj, x, y, z);
						}
					} else {
						if (part != null) {
							isGoodForInterior(part);
						} else {
							isBlockGoodForInterior(this.worldObj, x, y, z);
						}
					}
				}
			}
		}
	}

	protected abstract void isGoodForExteriorLevel(IMultiblockComponent part, int level) throws MultiblockValidationException;

	protected abstract void isGoodForInterior(IMultiblockComponent part) throws MultiblockValidationException;
}
