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
package forestry.pipes.network;

import java.io.IOException;

import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.PacketCoordinates;
import forestry.core.network.PacketId;
import forestry.pipes.EnumFilterType;

public class PacketTypeFilterChange extends PacketCoordinates {

	private int orientation;
	private int filter;

	public PacketTypeFilterChange(DataInputStreamForestry data) throws IOException {
		super(data);
	}

	public PacketTypeFilterChange(TileEntity tile, ForgeDirection orientation, EnumFilterType filter) {
		super(PacketId.PROP_SEND_FILTER_CHANGE_TYPE, tile);
		this.orientation = orientation.ordinal();
		this.filter = filter.ordinal();
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeShort(orientation);
		data.writeShort(filter);
	}

	@Override
	protected void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		orientation = data.readShort();
		filter = data.readShort();
	}

	public int getOrientation() {
		return orientation;
	}

	public int getFilter() {
		return filter;
	}
}
