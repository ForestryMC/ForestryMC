/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture;

import java.io.DataInputStream;

import net.minecraft.entity.player.EntityPlayer;

import forestry.apiculture.gui.ContainerImprinter;
import forestry.core.interfaces.IPacketHandler;
import forestry.core.network.PacketCoordinates;
import forestry.core.network.PacketIds;
import forestry.core.proxy.Proxies;

public class PacketHandlerApiculture implements IPacketHandler {

	@Override
	public void onPacketData(int packetID, DataInputStream data, EntityPlayer player) {

		try {

			switch (packetID) {
			case PacketIds.HABITAT_BIOME_POINTER:
				PacketCoordinates packetC = new PacketCoordinates();
				packetC.readData(data);
				Proxies.common.setBiomefinderCoordinates(player, packetC.getCoordinates());
				break;
			case PacketIds.IMPRINT_SELECTION_GET:
				onImprintSelectionGet(player);
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void onImprintSelectionGet(EntityPlayer playerEntity) {

		if (!(playerEntity.openContainer instanceof ContainerImprinter))
			return;

		((ContainerImprinter) playerEntity.openContainer).sendSelection(playerEntity);

	}

}
