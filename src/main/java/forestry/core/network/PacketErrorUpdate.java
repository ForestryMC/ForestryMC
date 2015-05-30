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
package forestry.core.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.tileentity.TileEntity;

import forestry.core.gadgets.TileForestry;
import forestry.core.proxy.Proxies;

public class PacketErrorUpdate extends PacketCoordinates {

	private TileForestry tileForestry;

	public PacketErrorUpdate(DataInputStream data) throws IOException {
		super(data);
	}

	public PacketErrorUpdate(TileForestry tileForestry) {
		super(PacketId.TILE_FORESTRY_ERROR_UPDATE, tileForestry);
		this.tileForestry = tileForestry;
	}

	@Override
	protected void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);
		tileForestry.writeErrorData(data);
	}

	@Override
	protected void readData(DataInputStream data) throws IOException {
		super.readData(data);

		TileEntity tile = getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof TileForestry) {
			((TileForestry) tile).readErrorData(data);
		}
	}
}
