/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.multiblock;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * IMultiblockController is used to handle the assembly of Multiblocks.
 * This class is created and updated by IMultiblockLogic and Forestry.
 * This is a minimal interface to hide the ugly details from the multiblock tile entities.
 */
public interface IMultiblockController {
	/**
	 * @return True if this multiblock machine is considered assembled and ready to go.
	 */
	boolean isAssembled();

	/**
	 * Call to force the controller to check the multiblock's validity.
	 * Use when important conditions around the multiblock change
	 * (i.e. Alveary slabs are removed and the alveary block detects it's neighbor changed)
	 * Changes to multiblock components are handled automatically and should not call this.
	 */
	void reassemble();

	/**
	 * @return A string representing the last error encountered when trying to assemble this
	 * multiblock, or null if there is no error.
	 */
	String getLastValidationError();

	/**
	 * @return all the multiblock components attached to this controller
	 */
	@Nonnull
	Collection<IMultiblockComponent> getComponents();
}
