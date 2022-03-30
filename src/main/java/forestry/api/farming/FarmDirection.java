/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import net.minecraft.core.Direction;

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
		return switch (forgeDirection) {
			case NORTH -> NORTH;
			case EAST -> EAST;
			case SOUTH -> SOUTH;
			case WEST -> WEST;
			default -> throw new IllegalArgumentException("Farm directions can only be NORTH, EAST, SOUTH, or WEST. Got: " + forgeDirection);
		};
	}
}
