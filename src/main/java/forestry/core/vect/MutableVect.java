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

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import forestry.api.farming.FarmDirection;

/**
 * Represents changeable positions or dimensions.
 */
public class MutableVect implements IVect {
	public BlockPos pos;

	public MutableVect(int x, int y, int z) {
		pos = new BlockPos(x, y, z);
	}
	
	public MutableVect(BlockPos pos) {
		this.pos = pos;
	}

	public MutableVect(EnumFacing direction) {
		int x = direction.getFrontOffsetX();
		int y = direction.getFrontOffsetY();
		int z = direction.getFrontOffsetZ();
		pos = new BlockPos(x, y, z);
	}

	public MutableVect(int[] dim) {
		if (dim.length != 3) {
			throw new RuntimeException("Cannot instantiate a vector with less or more than 3 points.");
		}

		int x = dim[0];
		int y = dim[1];
		int z = dim[2];
		pos = new BlockPos(x, y, z);
	}

	public MutableVect(IVect vect) {
		pos = vect.getPos();
	}

	public MutableVect add(IVect other) {
		int x = pos.getX() + other.getX();
		int y = pos.getY() + other.getY();
		int z = pos.getZ() + other.getZ();
		pos = new BlockPos(x, y, z);
		return this;
	}

	public MutableVect add(int xN, int yN, int zN) {
		int x = pos.getX() + xN;
		int y = pos.getY() + yN;
		int z = pos.getZ() + zN;
		pos = new BlockPos(x, y, z);
		return this;
	}

	@Override
	public MutableVect add(EnumFacing direction) {
		int x = pos.getX() + direction.getFrontOffsetX();
		int y = pos.getY() + direction.getFrontOffsetY();
		int z = pos.getZ() + direction.getFrontOffsetZ();
		pos = new BlockPos(x, y, z);
		return this;
	}

	@Override
	public MutableVect add(FarmDirection direction) {
		return add(direction.getDirection());
	}

	@Override
	public MutableVect add(BlockPos coordinates) {
		int x = pos.getX() + coordinates.getX();
		int y = pos.getY() + coordinates.getY();
		int z = pos.getZ() + coordinates.getZ();
		pos = new BlockPos(x, y, z);
		return this;
	}

	@Override
	public int[] toArray() {
		return new int[]{pos.getX(), pos.getY(), pos.getZ()};
	}

	public MutableVect multiply(float factor) {
		int x = (int) (pos.getX() * factor);
		int y = (int) (pos.getY() * factor);
		int z = (int) (pos.getZ() * factor);
		pos = new BlockPos(x, y, z);
		return this;
	}

	public boolean advancePositionInArea(Vect area) {
		// Increment z first until end reached
		if (pos.getZ() < area.pos.getZ() - 1) {
			pos = new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);
		} else {
			pos = new BlockPos(pos.getX(), pos.getY(), 0);

			if (pos.getX() < area.pos.getX() - 1) {
				pos = new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());
			} else {
				pos = new BlockPos(0, pos.getY(), pos.getZ());

				if (pos.getZ() < area.pos.getZ() - 1) {
					pos = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
				} else {
					return false;
				}
			}
		}

		return true;
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

	public void setX(int x) {
		pos = new BlockPos(x, getY(), getZ());
	}

	public void setY(int y) {
		pos = new BlockPos(getX(), y, getZ());
	}

	public void setZ(int z) {
		pos = new BlockPos(getX(), getY(), z);
	}
}
