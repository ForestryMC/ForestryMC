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

import forestry.greenhouse.api.greenhouse.IGreenhouseLimits;
import forestry.greenhouse.api.greenhouse.Position2D;
import forestry.core.network.PacketBufferForestry;

public class GreenhouseLimits implements IGreenhouseLimits {
	private final Position2D maximumCoordinates;
	private final Position2D minimumCoordinates;
	private final int height;
	private final int depth;

	public GreenhouseLimits(Position2D maximumCoordinates, Position2D minimumCoordinates, int height, int depth) {
		this.maximumCoordinates = maximumCoordinates;
		this.minimumCoordinates = minimumCoordinates;
		this.height = height;
		this.depth = depth;
	}

	public GreenhouseLimits(NBTTagCompound compound) {
		minimumCoordinates = new Position2D(compound.getCompoundTag("minimumCoordinates"));
		maximumCoordinates = new Position2D(compound.getCompoundTag("maximumCoordinates"));
		depth = compound.getInteger("depth");
		height = compound.getInteger("height");
	}

	public GreenhouseLimits(PacketBufferForestry data) throws IOException {
		minimumCoordinates = data.readPosition();
		maximumCoordinates = data.readPosition();
		depth = data.readInt();
		height = data.readInt();
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setTag("minimumCoordinates", minimumCoordinates.writeToNBT(new NBTTagCompound()));
		nbt.setTag("maximumCoordinates", maximumCoordinates.writeToNBT(new NBTTagCompound()));
		nbt.setInteger("depth", depth);
		nbt.setInteger("height", height);
		return nbt;
	}

	public void writeData(PacketBufferForestry data) {
		data.writePosition(minimumCoordinates);
		data.writePosition(maximumCoordinates);
		data.writeInt(depth);
		data.writeInt(height);
	}

	@Override
	public Position2D getMaximumCoordinates() {
		return maximumCoordinates;
	}

	@Override
	public Position2D getMinimumCoordinates() {
		return minimumCoordinates;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getDepth() {
		return depth;
	}
}
