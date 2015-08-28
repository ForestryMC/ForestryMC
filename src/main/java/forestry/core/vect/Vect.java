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

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import forestry.api.farming.FarmDirection;

/**
 * Represents an unchangeable position or dimensions.
 */
public class Vect implements IVect {
	public final BlockPos pos;

	public Vect(int[] dim) {
		if (dim.length != 3) {
			throw new RuntimeException("Cannot instantiate a vector with less or more than 3 points.");
		}

		int x = dim[0];
		int y = dim[1];
		int z = dim[2];
		pos = new BlockPos(x, y, z);
	}

	public Vect(IVect vect) {
		int x = vect.getX();
		int y = vect.getY();
		int z = vect.getZ();
		pos = new BlockPos(x, y, z);
	}

	public Vect(EnumFacing direction) {
		int x = direction.getFrontOffsetX();
		int y = direction.getFrontOffsetY();
		int z = direction.getFrontOffsetZ();
		pos = new BlockPos(x, y, z);
	}

	public Vect(int x, int y, int z) {
		pos = new BlockPos(x, y, z);
	}

	public Vect(BlockPos pos) {
		this.pos = pos;
	}

	public Vect(TileEntity entity) {
		this(entity.getPos());
	}

	public Vect(Entity entity) {
		int x = (int) Math.round(entity.posX);
		int y = (int) Math.round(entity.posY);
		int z = (int) Math.round(entity.posZ);
		pos = new BlockPos(x, y, z);
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
		return new Vect(pos.getX() + other.getX(), pos.getY() + other.getY(), pos.getZ() + other.getZ());
	}

	@Override
	public Vect add(int x, int y, int z) {
		return new Vect(pos.getX() + x,pos.getZ() + y, pos.getZ() + z);
	}

	@Override
	public Vect add(EnumFacing direction) {
		return add(direction.getFrontOffsetX(), direction.getFrontOffsetY(), direction.getFrontOffsetZ());
	}

	@Override
	public Vect add(FarmDirection direction) {
		return add(direction.getDirection());
	}

	@Override
	public Vect add(BlockPos pos) {
		return add(pos);
	}

	@Override
	public int[] toArray() {
		return new int[]{pos.getX(), pos.getY(), pos.getZ()};
	}

	public Vect multiply(int factor) {
		return new Vect(pos.getX() * factor, pos.getY() * factor, pos.getZ() * factor);
	}

	public Vect multiply(float factor) {
		return new Vect(Math.round(pos.getX() * factor), Math.round(pos.getY() * factor), Math.round(pos.getZ() * factor));
	}

	@Override
	public String toString() {
		return String.format("%sx%sx%s", pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + pos.getX();
		result = prime * result + pos.getY();
		result = prime * result + pos.getZ();
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
		return (other.pos.equals(pos));
	}

	@Override
	public int getX() {
		return pos.getX();
	}

	@Override
	public int getY() {
		return pos.getY();
	}

	@Override
	public int getZ() {
		return pos.getZ();
	}
	
	@Override
	public BlockPos getPos() {
		return pos;
	}
}
