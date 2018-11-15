package forestry.core.multiblock;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.multiblock.IMultiblockComponent;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.Translator;

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
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.small", minX, minY, minZ));
		}

		BlockPos maximumCoord = getMaximumCoord();
		BlockPos minimumCoord = getMinimumCoord();

		// Quickly check for exceeded dimensions
		int deltaX = maximumCoord.getX() - minimumCoord.getX() + 1;
		int deltaY = maximumCoord.getY() - minimumCoord.getY() + 1;
		int deltaZ = maximumCoord.getZ() - minimumCoord.getZ() + 1;

		int maxX = sizeLimits.getMaximumXSize();
		int maxY = sizeLimits.getMaximumYSize();
		int maxZ = sizeLimits.getMaximumZSize();

		if (maxX > 0 && deltaX > maxX) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.large.x", maxX));
		}
		if (maxY > 0 && deltaY > maxY) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.large.y", maxY));
		}
		if (maxZ > 0 && deltaZ > maxZ) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.large.z", maxZ));
		}
		if (deltaX < minX) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.small.x", minX));
		}
		if (deltaY < minY) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.small.y", minY));
		}
		if (deltaZ < minZ) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.small.z", minZ));
		}

		// Now we run a simple check on each block within that volume.
		// Any block deviating = NO DEAL SIR
		TileEntity te;
		IMultiblockComponent part;
		Class<? extends RectangularMultiblockControllerBase> myClass = this.getClass();

		for (int x = minimumCoord.getX(); x <= maximumCoord.getX(); x++) {
			for (int y = minimumCoord.getY(); y <= maximumCoord.getY(); y++) {
				for (int z = minimumCoord.getZ(); z <= maximumCoord.getZ(); z++) {
					// Okay, figure out what sort of block this should be.
					BlockPos pos = new BlockPos(x, y, z);
					te = TileUtil.getTile(world, pos);
					if (te instanceof IMultiblockComponent) {
						part = (IMultiblockComponent) te;

						// Ensure this part should actually be allowed within a cube of this controller's type
						if (!myClass.equals(part.getMultiblockLogic().getController().getClass())) {
							throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.invalid.part", Translator.translateToLocal(getUnlocalizedType())));
						}
					} else {
						// This is permitted so that we can incorporate certain non-multiblock parts inside interiors
						part = null;
					}

					// Validate block type against both part-level and material-level validators.
					int extremes = 0;

					if (x == minimumCoord.getX()) {
						extremes++;
					}
					if (y == minimumCoord.getY()) {
						extremes++;
					}
					if (z == minimumCoord.getZ()) {
						extremes++;
					}

					if (x == maximumCoord.getX()) {
						extremes++;
					}
					if (y == maximumCoord.getY()) {
						extremes++;
					}
					if (z == maximumCoord.getZ()) {
						extremes++;
					}

					if (extremes >= 1) {
						// Side
						int exteriorLevel = y - minimumCoord.getY();
						if (part != null) {
							isGoodForExteriorLevel(part, exteriorLevel);
						} else {
							isBlockGoodForExteriorLevel(exteriorLevel, this.world, pos);
						}
					} else {
						if (part != null) {
							isGoodForInterior(part);
						} else {
							isBlockGoodForInterior(this.world, pos);
						}
					}
				}
			}
		}
	}

	protected IMultiblockSizeLimits getSizeLimits() {
		return sizeLimits;
	}

	protected abstract void isGoodForExteriorLevel(IMultiblockComponent part, int level) throws MultiblockValidationException;

	protected abstract void isGoodForInterior(IMultiblockComponent part) throws MultiblockValidationException;
}
