/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.circuits;

public enum CircuitSocketType implements ICircuitSocketType {
	NONE("none"),
	FARM("forestry.farm"),
	ELECTRIC_ENGINE("forestry.electric.engine"),
	MACHINE("forestry.machine");

	private final String uid;

	CircuitSocketType(String uid) {
		this.uid = uid;
	}

	@Override
	public String getUid() {
		return uid;
	}

	@Override
	public boolean equals(ICircuitSocketType socketType) {
		return uid.equals(socketType.getUid());
	}
}
