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

import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import forestry.apiculture.gadgets.TileCandle;
import forestry.apiculture.gui.ContainerImprinter;
import forestry.core.interfaces.IPacketHandler;
import forestry.core.network.PacketCoordinates;
import forestry.core.network.PacketIds;
import forestry.core.proxy.Proxies;

public class PacketHandlerApiculture implements IPacketHandler {

	@Override
	public boolean onPacketData(int packetID, DataInputStream data, EntityPlayer player) throws IOException {

		switch (packetID) {
			case PacketIds.HABITAT_BIOME_POINTER: {
				PacketCoordinates packetC = new PacketCoordinates();
				packetC.readData(data);
				Proxies.common.setHabitatLocatorCoordinates(player, packetC.getCoordinates());
				return true;
			}
			case PacketIds.IMPRINT_SELECTION_GET: {
				onImprintSelectionGet(player);
				return true;
			}
			case PacketIds.CANDLE: {
				PacketUpdateCandle updateCandle = new PacketUpdateCandle();
				updateCandle.readData(data);
				onCandleUpdate(updateCandle);
				return true;
			}
		}

		return false;
	}

	private static void onImprintSelectionGet(EntityPlayer playerEntity) {

		if (!(playerEntity.openContainer instanceof ContainerImprinter)) {
			return;
		}

		((ContainerImprinter) playerEntity.openContainer).sendSelection(playerEntity);

	}

	private static void onCandleUpdate(PacketUpdateCandle updateCandle) {
		TileEntity tileEntity = updateCandle.getTarget(Proxies.common.getRenderWorld());
		if (tileEntity instanceof TileCandle) {
			((TileCandle) tileEntity).onPacketUpdate(updateCandle);
		}
	}

}
