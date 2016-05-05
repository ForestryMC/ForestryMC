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

import java.io.IOException;
import java.io.InputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.IThreadListener;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import forestry.core.proxy.Proxies;
import forestry.core.utils.Log;

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
		DataInputStreamForestry data = getStream(event.getPacket());
		EntityPlayerMP player = ((NetHandlerPlayServer) event.getHandler()).playerEntity;

		try {
			byte packetIdOrdinal = data.readByte();
			PacketIdServer packetId = PacketIdServer.VALUES[packetIdOrdinal];
			IForestryPacketServer packetHandler = packetId.getPacketHandler();
			checkThreadAndEnqueue(packetHandler, data, player, player.getServerWorld());
		} catch (IOException e) {
			Log.error("Failed to read packet.", e);
		}
	}

	@SubscribeEvent
	public void onPacket(ClientCustomPacketEvent event) {
		DataInputStreamForestry data = getStream(event.getPacket());
		EntityPlayer player = Proxies.common.getPlayer();

		try {
			byte packetIdOrdinal = data.readByte();
			PacketIdClient packetId = PacketIdClient.VALUES[packetIdOrdinal];
			IForestryPacketClient packetHandler = packetId.getPacketHandler();
			checkThreadAndEnqueue(packetHandler, data, player, Minecraft.getMinecraft());
		} catch (IOException e) {
			Log.error("Failed to read packet.", e);
		}
	}

	private static DataInputStreamForestry getStream(FMLProxyPacket fmlPacket) {
		InputStream is = new ByteBufInputStream(fmlPacket.payload());
		return new DataInputStreamForestry(is);
	}

	public void sendPacket(FMLProxyPacket packet, EntityPlayerMP player) {
		channel.sendTo(packet, player);
	}

	private static void checkThreadAndEnqueue(final IForestryPacketClient packet, final DataInputStreamForestry data, final EntityPlayer player, IThreadListener threadListener) {
		if (!threadListener.isCallingFromMinecraftThread()) {
			threadListener.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					try {
						packet.readData(data);
						packet.onPacketData(data, player);
					} catch (IOException e) {
						Log.error("Network Error", e);
					}
				}
			});
		}
	}

	private static void checkThreadAndEnqueue(final IForestryPacketServer packet, final DataInputStreamForestry data, final EntityPlayerMP player, IThreadListener threadListener) {
		if (!threadListener.isCallingFromMinecraftThread()) {
			threadListener.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					try {
						packet.readData(data);
						packet.onPacketData(data, player);
					} catch (IOException e) {
						Log.error("Network Error", e);
					}
				}
			});
		}
	}
}
