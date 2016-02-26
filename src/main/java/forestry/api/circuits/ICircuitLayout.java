/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.circuits;

import javax.annotation.Nonnull;

public interface ICircuitLayout {

	/** unique ID for this circuit layout */
	@Nonnull
	String getUID();

	/** localized name for this circuit layout */
	@Nonnull
	String getName();

	/** localized string for how this circuit layout is used */
	@Nonnull
	String getUsage();

	/**
	 * Specifies where a circuit layout is used.
	 */
	@Nonnull
	ICircuitSocketType getSocketType();

}
