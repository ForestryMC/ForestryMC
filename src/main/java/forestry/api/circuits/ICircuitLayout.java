/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.circuits;

public interface ICircuitLayout {

	/** unique ID for this circuit layout */
	String getUID();

	/** localized name for this circuit layout */
	String getName();

	/** localized string for how this circuit layout is used */
	String getUsage();

	/**
	 * Specifies where a circuit layout is used.
	 * @since Forestry 4.0
	 */
	ICircuitSocketType getSocketType();

}
