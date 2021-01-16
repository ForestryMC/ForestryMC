/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.network.PacketBuffer;

/**
 * Keeps track of all the IErrorStates for an object.
 * Create a new IErrorLogic instance for your object with ForestryAPI.errorStateRegistry.createErrorLogic().
 */
public interface IErrorLogic extends IErrorSource {

	/**
	 * Sets the errorState when condition is true, and unsets it when condition is false.
	 *
	 * @return condition
	 */
	boolean setCondition(boolean condition, IErrorState errorState);

	/**
	 * @return true if the error state is active.
	 */
	boolean contains(IErrorState state);

	/**
	 * @return true if there are any active error states.
	 */
	boolean hasErrors();

	/**
	 * Sets all active error states to false
	 */
	void clearErrors();

	/**
	 * Network serialization for syncing errors to the client from the server.
	 */
	void writeData(PacketBuffer data);

	void readData(PacketBuffer data);
}
