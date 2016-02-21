/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.circuits;

/**
 * Specifies where a circuit layout can be used. (i.e. farm, machine, engine, etc)
 * See CircuitSocketType for Forestry's uses.
 */
public interface ICircuitSocketType {
	/** unique identifier for this socket type */
	String getUid();

	/** comparison using uid */
	boolean equals(ICircuitSocketType socketType);
}
