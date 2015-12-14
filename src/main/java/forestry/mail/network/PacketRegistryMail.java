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

import forestry.core.network.PacketRegistry;
import forestry.mail.network.packets.PacketLetterInfoRequest;
import forestry.mail.network.packets.PacketLetterInfoResponse;
import forestry.mail.network.packets.PacketLetterTextSet;
import forestry.mail.network.packets.PacketPOBoxInfoUpdate;
import forestry.mail.network.packets.PacketTraderAddressRequest;
import forestry.mail.network.packets.PacketTraderAddressResponse;

public class PacketRegistryMail extends PacketRegistry {
	@Override
	public void registerPackets() {
		registerServerPacket(new PacketLetterInfoRequest());
		registerServerPacket(new PacketTraderAddressRequest());
		registerServerPacket(new PacketLetterTextSet());
		
		registerClientPacket(new PacketLetterInfoResponse());
		registerClientPacket(new PacketTraderAddressResponse());
		registerClientPacket(new PacketPOBoxInfoUpdate());
	}
}
