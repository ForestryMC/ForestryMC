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
package forestry.greenhouse.multiblock;

import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

import forestry.api.greenhouse.IGreenhouseSettings;
import forestry.api.greenhouse.Position2D;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;

public class GreenhouseSettings implements IGreenhouseSettings, IStreamable {
	private BlockPos startPosOffset;
	private Position2D[] edges = new Position2D[2];
	private Position2D minEdge;
	private Position2D maxEdge;
	private int lengthEastWest;
	private int lengthNorthSouth;
	private int height;
	private int depth;

	public GreenhouseSettings(BlockPos startPosOffset, int height, int depth, Position2D... edges) {
		this.startPosOffset = startPosOffset;
		this.height = height;
		this.depth = depth;
		this.edges = edges;
		this.maxEdge = Position2D.NULL_POSITION;
		this.minEdge = Position2D.NULL_POSITION;
		recalculateMinAndMaxEdges();
	}

	public GreenhouseSettings(BlockPos startPosOffset, int lengthEastWest, int lengthNorthSouth, int height, int depth) {
		this.startPosOffset = startPosOffset;
		this.lengthEastWest = lengthEastWest;
		this.lengthNorthSouth = lengthNorthSouth;
		this.height = height;
		this.depth = depth;
		this.edges[0] = minEdge = new Position2D(-lengthEastWest, -lengthNorthSouth);
		this.edges[1] = maxEdge = new Position2D(lengthEastWest, lengthNorthSouth);
	}

	public GreenhouseSettings(NBTTagCompound compound) {
		for (int i = 0; i < edges.length; i++) {
			edges[i] = Position2D.NULL_POSITION;
		}
		readFromNBT(compound);
	}

	public GreenhouseSettings(PacketBufferForestry data) throws IOException {
		for (int i = 0; i < edges.length; i++) {
			edges[i] = Position2D.NULL_POSITION;
		}
		readData(data);
	}

	@Override
	public BlockPos getStartPosOffset() {
		return startPosOffset;
	}

	@Override
	public void setStartPosOffset(BlockPos startPosOffset) {
		this.startPosOffset = startPosOffset;
	}

	@Override
	public int getLengthEastWest() {
		return lengthEastWest;
	}

	@Override
	public void setLengthEastWest(int lengthEastWest) {
		this.lengthEastWest = lengthEastWest;
	}

	@Override
	public int getLengthNorthSouth() {
		return lengthNorthSouth;
	}

	@Override
	public void setLengthNorthSouth(int lengthNorthSouth) {
		this.lengthNorthSouth = lengthNorthSouth;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public void setDepth(int depth) {
		this.depth = depth;
	}

	@Override
	public Position2D[] getEdges() {
		return edges;
	}

	public Position2D getMaxEdge() {
		return maxEdge;
	}

	public Position2D getMinEdge() {
		return minEdge;
	}

	@Override
	public IGreenhouseSettings clamp(IGreenhouseSettings clampSettings) {
		Position2D minEdge = this.minEdge.clamp(clampSettings.getMinEdge(), clampSettings.getMaxEdge());
		Position2D maxEdge = this.maxEdge.clamp(clampSettings.getMinEdge(), clampSettings.getMaxEdge());
		GreenhouseSettings settings = new GreenhouseSettings(this.startPosOffset, height, depth, minEdge, maxEdge);
		settings.recalculateMinAndMaxEdges();
		return settings;
	}

	@Override
	public void setEdge(int edgeId, Position2D position) {
		setEdge(edgeId, position, true);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		startPosOffset = NBTUtil.getPosFromTag(nbt.getCompoundTag("Offset"));
		lengthNorthSouth = nbt.getInteger("lengthNorthSouth");
		lengthEastWest = nbt.getInteger("lengthEastWest");
		depth = nbt.getInteger("depth");
		height = nbt.getInteger("height");
		int[] edges = nbt.getIntArray("edges");
		if (edges.length == 4) {
			setEdge(0, new Position2D(edges[0], edges[1]), false);
			setEdge(1, new Position2D(edges[2], edges[3]), false);
			recalculateMinAndMaxEdges();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setTag("Offset", NBTUtil.createPosTag(startPosOffset));
		nbt.setInteger("lengthNorthSouth", lengthNorthSouth);
		nbt.setInteger("lengthEastWest", lengthEastWest);
		nbt.setInteger("depth", depth);
		nbt.setInteger("height", height);
		nbt.setIntArray("edges", new int[]{edges[0].getX(), edges[0].getZ(), edges[1].getX(), edges[1].getZ()});
		return nbt;
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		data.writeBlockPos(startPosOffset);
		data.writeInt(lengthNorthSouth);
		data.writeInt(lengthEastWest);
		data.writeInt(depth);
		data.writeInt(height);
		data.writePosition(edges[0]);
		data.writePosition(edges[1]);
	}

	@Override
	public void readData(PacketBufferForestry data) throws IOException {
		startPosOffset = data.readBlockPos();
		lengthNorthSouth = data.readInt();
		lengthEastWest = data.readInt();
		depth = data.readInt();
		height = data.readInt();
		setEdge(0, data.readPosition());
		setEdge(1, data.readPosition());
	}

	private void setEdge(int edgeId, Position2D position, boolean recalculate) {
		edges[edgeId] = position;
		if (recalculate) {
			recalculateMinAndMaxEdges();
		}
	}

	private void recalculateMinAndMaxEdges() {
		int minX = Integer.MAX_VALUE;
		int minZ = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxZ = Integer.MIN_VALUE;

		for (Position2D position : edges) {
			if (position.getX() < minX) {
				minX = position.getX();
			}
			if (position.getX() > maxX) {
				maxX = position.getX();
			}
			if (position.getZ() < minZ) {
				minZ = position.getZ();
			}
			if (position.getZ() > maxZ) {
				maxZ = position.getZ();
			}
		}
		minEdge = new Position2D(minX, minZ);
		maxEdge = new Position2D(maxX, maxZ);
	}
}
