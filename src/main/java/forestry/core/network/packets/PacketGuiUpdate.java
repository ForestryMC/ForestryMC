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

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketIdClient;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.ILocatable;

public class PacketGuiUpdate extends PacketCoordinates implements IForestryPacketClient {

	private IStreamableGui guiDataTile;

	public PacketGuiUpdate() {
	}

	public <T extends IStreamableGui & ILocatable> PacketGuiUpdate(T guiDataTile) {
		super(guiDataTile.getCoordinates());
		this.guiDataTile = guiDataTile;
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		guiDataTile.writeGuiData(data);
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		TileEntity tile = getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof IStreamableGui) {
			((IStreamableGui) tile).readGuiData(data);
		}
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.GUI_UPDATE;
	}
}
