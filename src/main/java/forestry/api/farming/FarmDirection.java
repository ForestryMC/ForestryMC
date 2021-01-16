/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import net.minecraft.util.Direction;

public enum FarmDirection {
	NORTH(Direction.NORTH),
	EAST(Direction.EAST),
	SOUTH(Direction.SOUTH),
	WEST(Direction.WEST);

	private final Direction forgeDirection;

	FarmDirection(Direction forgeDirection) {
		this.forgeDirection = forgeDirection;
	}

	public Direction getFacing() {
		return forgeDirection;
	}

	public static FarmDirection getFarmDirection(Direction forgeDirection) {
		switch (forgeDirection) {
			case NORTH:
				return NORTH;
			case EAST:
				return EAST;
			case SOUTH:
				return SOUTH;
			case WEST:
				return WEST;
			default:
				throw new IllegalArgumentException("Farm directions can only be NORTH, EAST, SOUTH, or WEST. Got: " + forgeDirection);
		}
	}
}
