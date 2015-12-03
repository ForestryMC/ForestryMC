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

import java.io.InputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

import forestry.core.proxy.Proxies;

import io.netty.buffer.ByteBufInputStream;

public class PacketHandler {
	public static final String channelId = "FOR";
	private final FMLEventChannel channel;

	public PacketHandler() {
		channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(channelId);
		channel.register(this);
	}

	@SubscribeEvent
	public void onPacket(ServerCustomPacketEvent event) {
		DataInputStreamForestry data = getStream(event.packet);
		EntityPlayerMP player = ((NetHandlerPlayServer) event.handler).playerEntity;

		try {
			byte packetIdOrdinal = data.readByte();
			PacketIdServer packetId = PacketIdServer.VALUES[packetIdOrdinal];
			IForestryPacketServer packetHandler = packetId.getPacketHandler();
			packetHandler.readData(data);
			packetHandler.onPacketData(data, player);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onPacket(ClientCustomPacketEvent event) {
		DataInputStreamForestry data = getStream(event.packet);
		EntityPlayer player = Proxies.common.getPlayer();

		try {
			byte packetIdOrdinal = data.readByte();
			PacketIdClient packetId = PacketIdClient.VALUES[packetIdOrdinal];
			IForestryPacketClient packetHandler = packetId.getPacketHandler();
			packetHandler.readData(data);
			packetHandler.onPacketData(data, player);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static DataInputStreamForestry getStream(FMLProxyPacket fmlPacket) {
		InputStream is = new ByteBufInputStream(fmlPacket.payload());
		return new DataInputStreamForestry(is);
	}

	public void sendPacket(FMLProxyPacket packet, EntityPlayerMP player) {
		channel.sendTo(packet, player);
	}

}
