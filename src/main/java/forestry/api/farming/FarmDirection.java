/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import net.minecraftforge.common.util.ForgeDirection;

public enum FarmDirection {
	NORTH(ForgeDirection.NORTH),
	EAST(ForgeDirection.EAST),
	SOUTH(ForgeDirection.SOUTH),
	WEST(ForgeDirection.WEST);

	private final ForgeDirection forgeDirection;

	FarmDirection(ForgeDirection forgeDirection) {
		this.forgeDirection = forgeDirection;
	}

	public ForgeDirection getForgeDirection() {
		return forgeDirection;
	}

	public static FarmDirection getFarmDirection(ForgeDirection forgeDirection) {
		switch (forgeDirection) {
			case NORTH: return NORTH;
			case EAST: return EAST;
			case SOUTH: return SOUTH;
			case WEST: return WEST;
		}
		return null;
	}
}
