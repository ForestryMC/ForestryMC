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
package forestry.core.utils;

/**
 * Generic class to represent a position or dimensions.
 */
public class Vect {
	public int x;
	public int y;
	public int z;

	public Vect(int[] dim) {
		if (dim.length != 3)
			throw new RuntimeException("Cannot instantiate a vector with less or more than 3 points.");

		this.x = dim[0];
		this.y = dim[1];
		this.z = dim[2];
	}

	public Vect(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vect add(Vect other) {
		Vect result = new Vect(x, y, z);
		result.x += other.x;
		result.y += other.y;
		result.z += other.z;
		return result;
	}

	public Vect multiply(float factor) {
		Vect result = new Vect(x, y, z);
		result.x *= factor;
		result.y *= factor;
		result.z *= factor;
		return result;
	}

	@Override
	public String toString() {
		return String.format("%sx%sx%s", x, y, z);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vect other = (Vect) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}
}
