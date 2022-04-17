/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import com.google.common.collect.ImmutableSet;

public interface IErrorSource {
	/**
	 * @return the active error states. An empty Set indicates no errors.
	 */
	ImmutableSet<IErrorState> getErrorStates();
}
