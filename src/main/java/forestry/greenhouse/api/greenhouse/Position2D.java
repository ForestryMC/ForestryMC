/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.greenhouse;

import com.google.common.base.MoreObjects;

import javax.annotation.concurrent.Immutable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

import forestry.api.core.INbtWritable;

@Immutable
public class Position2D implements Comparable<Position2D>, INbtWritable {
	public static final Position2D NULL_POSITION = new Position2D(0, 0);
	/** X coordinate */
	private final int x;
	/** Z coordinate */
	private final int z;

	public Position2D(Vec3i pos) {
		this(pos.getX(), pos.getZ());
	}

	public Position2D(Position2D pos) {
		this(pos.getX(), pos.getZ());
	}

	public Position2D(int xIn, int zIn) {
		this.x = xIn;
		this.z = zIn;
	}

	public Position2D(NBTTagCompound nbtTagCompound) {
		this.x = nbtTagCompound.getInteger("xPosition");
		this.z = nbtTagCompound.getInteger("zPosition");
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o instanceof Vec3i) {
			Vec3i position = (Vec3i) o;
			return this.getX() == position.getX() && this.getZ() == position.getZ();
		} else if (!(o instanceof Position2D)) {
			return false;
		} else {
			Position2D position = (Position2D) o;
			return this.getX() == position.getX() && this.getZ() == position.getZ();
		}
	}

	public Position2D clamp(Position2D min, Position2D max) {
		int x = MathHelper.clamp(this.x, min.getX(), max.getX());
		int z = MathHelper.clamp(this.z, min.getZ(), max.getZ());
		return new Position2D(x, z);
	}

	public Position2D add(int x, int z) {
		return x == 0 && z == 0 ? this : new Position2D(this.getX() + x, this.getZ() + z);
	}

	public int hashCode() {
		return (this.getZ() + this.getX() * 31);
	}

	public int compareTo(Position2D o) {
		return this.getZ() == o.getZ() ? this.getX() - o.getX() : this.getZ() - o.getZ();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("xPosition", x);
		nbt.setInteger("zPosition", z);
		return nbt;
	}

	/**
	 * Gets the X coordinate.
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * Gets the Z coordinate.
	 */
	public int getZ() {
		return this.z;
	}

	public double getDistance(int xIn, int zIn) {
		double d0 = (double) (this.getX() - xIn);
		double d2 = (double) (this.getZ() - zIn);
		return Math.sqrt(d0 * d0 + d2 * d2);
	}

	public double distanceSq(double toX, double toZ) {
		double d0 = (double) this.getX() - toX;
		double d2 = (double) this.getZ() - toZ;
		return d0 * d0 + d2 * d2;
	}

	public double distanceSqToCenter(double xIn, double zIn) {
		double d0 = (double) this.getX() + 0.5D - xIn;
		double d2 = (double) this.getZ() + 0.5D - zIn;
		return d0 * d0 + d2 * d2;
	}

	public double distanceSq(Position2D to) {
		return this.distanceSq((double) to.getX(), (double) to.getZ());
	}

	public String toString() {
		return MoreObjects.toStringHelper(this).add("x", this.getX()).add("z", this.getZ()).toString();
	}
}