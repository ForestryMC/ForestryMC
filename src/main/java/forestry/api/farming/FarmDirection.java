/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import net.minecraft.util.EnumFacing;

public enum FarmDirection {
	NORTH(EnumFacing.NORTH),
	EAST(EnumFacing.EAST),
	SOUTH(EnumFacing.SOUTH),
	WEST(EnumFacing.WEST);

	private final EnumFacing forgeDirection;

	FarmDirection(EnumFacing forgeDirection) {
		this.forgeDirection = forgeDirection;
	}

	public EnumFacing getFacing() {
		return forgeDirection;
	}

	public static FarmDirection getFarmDirection(EnumFacing forgeDirection) {
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
				throw new IllegalArgumentException("Farm directios can only be NORTH, EAST, SOUTH, or WEST. Got: " + forgeDirection);
		}
	}
}
