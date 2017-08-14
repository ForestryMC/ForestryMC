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
package forestry.greenhouse.network;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.network.IPacketRegistry;
import forestry.core.network.PacketIdClient;
import forestry.core.network.PacketIdServer;
import forestry.greenhouse.network.packets.PacketCamouflageSelectionClient;
import forestry.greenhouse.network.packets.PacketCamouflageSelectionServer;
import forestry.greenhouse.network.packets.PacketGreenhouseData;
import forestry.greenhouse.network.packets.PacketGreenhouseDataRequest;
import forestry.greenhouse.network.packets.PacketSelectClimateTargeted;

public class PacketRegistryGreenhouse implements IPacketRegistry {
	@Override
	public void registerPacketsServer() {
		PacketIdServer.CAMOUFLAGE_SELECTION.setPacketHandler(new PacketCamouflageSelectionServer.Handler());
		PacketIdServer.SELECT_CLIMATE_TARGETED.setPacketHandler(new PacketSelectClimateTargeted.Handler());
		PacketIdServer.GREENHOUSE_DATA_REQUEST.setPacketHandler(new PacketGreenhouseDataRequest.Handler());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerPacketsClient() {
		PacketIdClient.CAMOUFLAGE_SELECTION.setPacketHandler(new PacketCamouflageSelectionClient.Handler());
		PacketIdClient.GREENHOUSE_DATA.setPacketHandler(new PacketGreenhouseData.Handler());
	}
}
