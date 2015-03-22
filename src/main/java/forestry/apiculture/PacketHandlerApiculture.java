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
package forestry.apiculture;

import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import forestry.apiculture.gui.ContainerImprinter;
import forestry.core.interfaces.IPacketHandler;
import forestry.core.network.PacketCoordinates;
import forestry.core.network.PacketIds;
import forestry.core.proxy.Proxies;

public class PacketHandlerApiculture implements IPacketHandler {

	@Override
	public boolean onPacketData(int packetID, DataInputStream data, EntityPlayer player) throws IOException {

		switch (packetID) {
			case PacketIds.HABITAT_BIOME_POINTER:
				PacketCoordinates packetC = new PacketCoordinates();
				packetC.readData(data);
				Proxies.common.setHabitatLocatorCoordinates(player, packetC.getCoordinates());
				return true;
			case PacketIds.IMPRINT_SELECTION_GET:
				onImprintSelectionGet(player);
				return true;
		}

		return false;
	}

	private void onImprintSelectionGet(EntityPlayer playerEntity) {

		if (!(playerEntity.openContainer instanceof ContainerImprinter)) {
			return;
		}

		((ContainerImprinter) playerEntity.openContainer).sendSelection(playerEntity);

	}

}
