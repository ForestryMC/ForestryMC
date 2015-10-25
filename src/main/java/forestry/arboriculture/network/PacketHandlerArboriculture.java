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
package forestry.arboriculture.network;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.IPacketHandler;
import forestry.core.network.PacketId;

public class PacketHandlerArboriculture implements IPacketHandler {

	@Override
	public boolean onPacketData(PacketId packetID, DataInputStreamForestry data, EntityPlayer player)
			throws IOException {

		switch (packetID) {
		case RIPENING_UPDATE: {
			PacketRipeningUpdate.onPacketData(data);
			return true;
		}
		}

		return false;
	}

}
