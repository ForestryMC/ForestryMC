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
package forestry.apiculture.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;
import forestry.core.network.packets.PacketCoordinates;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.IActivatable;

public class PacketActiveUpdate extends PacketCoordinates implements IForestryPacketClient {

	private boolean active;

	public PacketActiveUpdate() {
	}

	public PacketActiveUpdate(IActivatable tile) {
		super(tile.getCoordinates());
		this.active = tile.isActive();
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.TILE_FORESTRY_ACTIVE;
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeBoolean(active);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		active = data.readBoolean();
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) {
		TileEntity tile = getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof IActivatable) {
			((IActivatable) tile).setActive(active);
		}
	}
}
