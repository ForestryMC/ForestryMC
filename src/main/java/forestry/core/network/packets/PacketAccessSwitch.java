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

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

import forestry.core.access.IRestrictedAccess;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketIdServer;
import forestry.core.tiles.ILocatable;

public class PacketAccessSwitch extends PacketCoordinates implements IForestryPacketServer {

	public PacketAccessSwitch() {
	}

	public PacketAccessSwitch(ILocatable tile) {
		super(tile.getCoordinates());
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayerMP player) throws IOException {
		TileEntity tile = getTarget(player.worldObj);

		if (tile instanceof IRestrictedAccess) {
			IRestrictedAccess restrictedAccessTile = (IRestrictedAccess) tile;
			restrictedAccessTile.getAccessHandler().switchAccess(player);
		}
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.ACCESS_SWITCH;
	}
}
