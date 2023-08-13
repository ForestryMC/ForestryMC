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
package forestry.core.network;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.network.ICustomPacket;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import forestry.core.config.Constants;

public class PacketHandlerServer {

	private static final Logger LOGGER = LogManager.getLogger();

	public static final ResourceLocation CHANNEL_ID = new ResourceLocation(Constants.MOD_ID, "channel");
	public static final String VERSION = "1.0.0";

	public void onPacket(NetworkEvent.ClientCustomPayloadEvent event) {
		PacketBufferForestry data = new PacketBufferForestry(event.getPayload());
		NetworkEvent.Context ctx = event.getSource().get();
		ServerPlayer player = ctx.getSender();

		if (player == null) {
			LOGGER.warn("the player was null, event: {}", event);
			return;
		}

		byte packetIdOrdinal = data.readByte();
		PacketIdServer packetId = PacketIdServer.VALUES[packetIdOrdinal];
		IForestryPacketHandlerServer packetHandler = packetId.getPacketHandler();
		packetHandler.onPacketData(data, player);
		event.getSource().get().setPacketHandled(true);
	}

	public static void sendPacket(IForestryPacketClient packet, ServerPlayer player) {
		Pair<FriendlyByteBuf, Integer> packetData = packet.getPacketData();
		ICustomPacket<Packet<?>> payload = NetworkDirection.PLAY_TO_CLIENT.buildPacket(packetData, PacketHandlerServer.CHANNEL_ID);
		player.connection.send(payload.getThis());
	}
}
