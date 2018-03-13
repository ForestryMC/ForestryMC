package forestry.core.multiblock;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;

/**
 * An exception thrown when trying to validate a multiblock. Requires a string describing why the multiblock
 * could not assemble.
 *
 * @author Erogenous Beef
 */
public class MultiblockValidationException extends Exception {

	@Nullable
	private BlockPos position;

	public MultiblockValidationException(String reason) {
		super(reason);
	}

	public MultiblockValidationException(String reason, BlockPos position) {
		super(reason);
		this.position = position;
	}

	public BlockPos getPosition() {
		return position;
	}
}
