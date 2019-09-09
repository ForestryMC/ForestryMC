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
package forestry.mail.network;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.network.IPacketRegistry;
import forestry.core.network.PacketIdClient;
import forestry.core.network.PacketIdServer;
import forestry.mail.network.packets.PacketLetterInfoRequest;
import forestry.mail.network.packets.PacketLetterInfoResponse;
import forestry.mail.network.packets.PacketLetterTextSet;
import forestry.mail.network.packets.PacketPOBoxInfoResponse;
import forestry.mail.network.packets.PacketTraderAddressRequest;
import forestry.mail.network.packets.PacketTraderAddressResponse;

public class PacketRegistryMail implements IPacketRegistry {
	@Override
	public void registerPacketsServer() {
		PacketIdServer.LETTER_INFO_REQUEST.setPacketHandler(new PacketLetterInfoRequest.Handler());
		PacketIdServer.TRADING_ADDRESS_REQUEST.setPacketHandler(new PacketTraderAddressRequest.Handler());
		PacketIdServer.LETTER_TEXT_SET.setPacketHandler(new PacketLetterTextSet.Handler());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerPacketsClient() {
		PacketIdClient.LETTER_INFO_RESPONSE.setPacketHandler(new PacketLetterInfoResponse.Handler());
		PacketIdClient.TRADING_ADDRESS_RESPONSE.setPacketHandler(new PacketTraderAddressResponse.Handler());
		PacketIdClient.POBOX_INFO_RESPONSE.setPacketHandler(new PacketPOBoxInfoResponse.Handler());
	}
}
