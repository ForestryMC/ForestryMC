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
package forestry.apiculture.network;

import java.io.IOException;

import net.minecraft.tileentity.TileEntity;

import forestry.core.interfaces.IActivatable;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.PacketCoordinates;
import forestry.core.network.PacketId;
import forestry.core.proxy.Proxies;

public class PacketActiveUpdate extends PacketCoordinates {

	private boolean active;

	public static void onPacketData(DataInputStreamForestry data) throws IOException {
		new PacketActiveUpdate(data);
	}

	private PacketActiveUpdate(DataInputStreamForestry data) throws IOException {
		super(data);
	}

	public PacketActiveUpdate(IActivatable tile) {
		super(PacketId.TILE_FORESTRY_ACTIVE, tile.getCoordinates());
		this.active = tile.isActive();
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeBoolean(active);
	}

	@Override
	protected void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);

		active = data.readBoolean();

		TileEntity tile = getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof IActivatable) {
			((IActivatable) tile).setActive(active);
		}
	}
}
