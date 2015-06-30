package forestry.core.multiblock;

/**
 * An exception thrown when trying to validate a multiblock. Requires a string describing why the multiblock
 * could not assemble.
 * @author Erogenous Beef
 */
public class MultiblockValidationException extends Exception {

	public MultiblockValidationException(String reason) {
		super(reason);
	}
}
