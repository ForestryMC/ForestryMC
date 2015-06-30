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

import java.io.IOException;

import net.minecraft.tileentity.TileEntity;

import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorLogicSource;
import forestry.core.proxy.Proxies;

public class PacketErrorUpdate extends PacketCoordinates {

	private IErrorLogic errorLogic;

	public static void onPacketData(DataInputStreamForestry data) throws IOException {
		new PacketErrorUpdate(data);
	}

	public PacketErrorUpdate(DataInputStreamForestry data) throws IOException {
		super(data);
	}

	public PacketErrorUpdate(TileEntity tile, IErrorLogicSource errorLogicSource) {
		super(PacketId.TILE_FORESTRY_ERROR_UPDATE, tile);
		this.errorLogic = errorLogicSource.getErrorLogic();
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		errorLogic.writeData(data);
	}

	@Override
	protected void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);

		TileEntity tile = getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof IErrorLogicSource) {
			IErrorLogicSource errorSourceTile = (IErrorLogicSource) tile;
			errorLogic = errorSourceTile.getErrorLogic();
			errorLogic.readData(data);
		}
	}
}
