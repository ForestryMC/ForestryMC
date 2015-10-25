package forestry.core.multiblock;

import javax.annotation.Nonnull;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ChunkCoordIntPair;

/*
 * Simple wrapper class for XYZ coordinates.
 */
public class CoordTriplet implements Comparable {
	public BlockPos pos;

	public CoordTriplet(BlockPos pos) {
		this.pos = pos;
	}

	public CoordTriplet(int x, int y, int z) {
		this.pos = new BlockPos(x, y, z);
	}

	public int getChunkX() {
		return pos.getX() >> 4;
	}

	public int getChunkZ() {
		return pos.getZ() >> 4;
	}

	public long getChunkXZHash() {
		return ChunkCoordIntPair.chunkXZ2Int(pos.getX() >> 4, pos.getZ() >> 4);
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		} else if (other instanceof CoordTriplet) {
			CoordTriplet otherTriplet = (CoordTriplet) other;
			return pos.equals(otherTriplet.pos);
		} else {
			return false;
		}
	}

	public void translate(EnumFacing dir) {
		int x = pos.getX() + dir.getFrontOffsetX();
		int y = pos.getY() + dir.getFrontOffsetY();
		int z = pos.getZ() + dir.getFrontOffsetZ();
		pos = new BlockPos(x, y, z);
	}

	public boolean equals(BlockPos pos) {
		return pos.equals(pos);
	}

	// Suggested implementation from NetBeans 7.1
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 71 * hash + pos.getX();
		hash = 71 * hash + pos.getY();
		hash = 71 * hash + pos.getZ();
		return hash;
	}

	public CoordTriplet copy() {
		return new CoordTriplet(pos);
	}

	public void copy(CoordTriplet other) {
		pos = other.pos;
	}

	///// IComparable

	@Override
	public int compareTo(@Nonnull Object o) {
		if (o instanceof CoordTriplet) {
			CoordTriplet other = (CoordTriplet) o;
			if (this.pos.getX() < other.pos.getX()) {
				return -1;
			} else if (this.pos.getX() > other.pos.getX()) {
				return 1;
			} else if (this.pos.getY() < other.pos.getY()) {
				return -1;
			} else if (this.pos.getY() > other.pos.getY()) {
				return 1;
			} else if (this.pos.getZ() < other.pos.getZ()) {
				return -1;
			} else if (this.pos.getZ() > other.pos.getZ()) {
				return 1;
			} else {
				return 0;
			}
		}
		return 0;
	}

	@Override
	public String toString() {
		return String.format("(%d, %d, %d)", this.pos.getX(), this.pos.getY(), this.pos.getZ());
	}

	public int compareTo(int xCoord, int yCoord, int zCoord) {
		if (this.pos.getX() < xCoord) {
			return -1;
		} else if (this.pos.getX() > xCoord) {
			return 1;
		} else if (this.pos.getY() < yCoord) {
			return -1;
		} else if (this.pos.getY() > yCoord) {
			return 1;
		} else if (this.pos.getZ() < zCoord) {
			return -1;
		} else if (this.pos.getZ() > zCoord) {
			return 1;
		} else {
			return 0;
		}
	}
}
