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
package forestry.core.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorLogicSource;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;
import forestry.core.proxy.Proxies;

public class PacketErrorUpdate extends PacketCoordinates implements IForestryPacketClient {

	private IErrorLogic errorLogic;

	public PacketErrorUpdate() {
	}

	public PacketErrorUpdate(TileEntity tile, IErrorLogicSource errorLogicSource) {
		super(tile);
		this.errorLogic = errorLogicSource.getErrorLogic();
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		errorLogic.writeData(data);
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		TileEntity tile = getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof IErrorLogicSource) {
			IErrorLogicSource errorSourceTile = (IErrorLogicSource) tile;
			errorLogic = errorSourceTile.getErrorLogic();
			errorLogic.readData(data);
		}
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.ERROR_UPDATE;
	}
}
