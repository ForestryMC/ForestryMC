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
package forestry.core.utils.vect;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import forestry.api.farming.FarmDirection;

/**
 * Represents an unchangeable position or dimensions.
 */
public class Vect extends IVect {

	public Vect(int[] dim) {
		super(dim[0], dim[1], dim[2]);
		if (dim.length != 3) {
			throw new RuntimeException("Cannot instantiate a vector with less or more than 3 points.");
		}
	}

	public Vect(IVect vect) {
		super(vect.getX(), vect.getY(), vect.getZ());
	}

	public Vect(EnumFacing direction) {
		super(direction.getFrontOffsetX(), direction.getFrontOffsetY(), direction.getFrontOffsetZ());
	}

	public Vect(int x, int y, int z) {
		super(x, y, z);
	}

	public Vect(BlockPos pos) {
		super(pos);
	}

	public Vect(TileEntity entity) {
		this(entity.getPos());
	}

	public Vect(Entity entity) {
		super(entity);
	}

	public static Vect getRandomPositionInArea(Random random, IVect area) {
		int x = random.nextInt(area.getX());
		int y = random.nextInt(area.getY());
		int z = random.nextInt(area.getZ());
		return new Vect(x, y, z);
	}

	public static Vect add(IVect... vects) {
		int x = 0;
		int y = 0;
		int z = 0;
		for (IVect vect : vects) {
			x += vect.getX();
			y += vect.getY();
			z += vect.getZ();
		}
		return new Vect(x, y, z);
	}

	@Override
	public Vect add(IVect other) {
		return new Vect(getX() + other.getX(), getY() + other.getY(), getZ() + other.getZ());
	}

	@Override
	public Vect add(int x, int y, int z) {
		return new Vect(getX() + x, getY() + y, getZ() + z);
	}

	@Override
	public Vect add(EnumFacing direction) {
		return add(direction.getFrontOffsetX(), direction.getFrontOffsetY(), direction.getFrontOffsetZ());
	}

	@Override
	public Vect add(FarmDirection direction) {
		return add(direction.getForgeDirection());
	}

	@Override
	public Vect add(BlockPos pos) {
		return add(pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public int[] toArray() {
		return new int[]{getX(), getY(), getZ()};
	}

	public Vect multiply(int factor) {
		return new Vect(getX() * factor, getY() * factor, getZ() * factor);
	}

	public Vect multiply(float factor) {
		return new Vect(Math.round(getX() * factor), Math.round(getY() * factor), Math.round(getZ() * factor));
	}

	@Override
	public String toString() {
		return String.format("%sx%sx%s", getX(), getY(), getZ());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getX();
		result = prime * result + getY();
		result = prime * result + getZ();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Vect)) {
			return false;
		}
		Vect other = (Vect) obj;
		return (getX() == other.getX()) && (getY() == other.getY()) && (getZ() == other.getZ());
	}
}
