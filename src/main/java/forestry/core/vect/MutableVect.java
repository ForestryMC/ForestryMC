/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.vect;

import net.minecraft.util.ChunkCoordinates;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.farming.FarmDirection;

/**
 * Represents changeable positions or dimensions.
 */
public class MutableVect implements IVect {
	public int x;
	public int y;
	public int z;

	public MutableVect(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public MutableVect(int[] dim) {
		if (dim.length != 3) {
			throw new RuntimeException("Cannot instantiate a vector with less or more than 3 points.");
		}

		this.x = dim[0];
		this.y = dim[1];
		this.z = dim[2];
	}

	public MutableVect(ChunkCoordinates coordinates) {
		this.x = coordinates.posX;
		this.y = coordinates.posY;
		this.z = coordinates.posZ;
	}

	public MutableVect(IVect vect) {
		this.x = vect.getX();
		this.y = vect.getY();
		this.z = vect.getZ();
	}

	public MutableVect add(IVect other) {
		x += other.getX();
		y += other.getY();
		z += other.getZ();
		return this;
	}

	public MutableVect add(int x, int y, int z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	@Override
	public MutableVect add(ForgeDirection direction) {
		this.x += direction.offsetX;
		this.y += direction.offsetY;
		this.z += direction.offsetZ;
		return this;
	}

	@Override
	public IVect add(FarmDirection direction) {
		return add(direction.getForgeDirection());
	}

	@Override
	public int[] toArray() {
		return new int[]{x, y, z};
	}

	public MutableVect multiply(float factor) {
		this.x *= factor;
		this.y *= factor;
		this.z *= factor;
		return this;
	}

	public boolean advancePositionInArea(Vect area) {
		// Increment z first until end reached
		if (z < area.z - 1) {
			z++;
		} else {
			z = 0;

			if (x < area.x - 1) {
				x++;
			} else {
				x = 0;

				if (y < area.y - 1) {
					y++;
				} else {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getZ() {
		return z;
	}
}
