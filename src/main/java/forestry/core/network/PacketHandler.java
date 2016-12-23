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

import com.google.common.base.Preconditions;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Log;
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

public class PacketHandler {
	public static final String channelId = "FOR";
	private final FMLEventChannel channel;

	public PacketHandler() {
		channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(channelId);
		channel.register(this);
	}

	@SubscribeEvent
	public void onPacket(ServerCustomPacketEvent event) {
		PacketBufferForestry data = new PacketBufferForestry(event.getPacket().payload());
		EntityPlayerMP player = ((NetHandlerPlayServer) event.getHandler()).playerEntity;

		byte packetIdOrdinal = data.readByte();
		PacketIdServer packetId = PacketIdServer.VALUES[packetIdOrdinal];
		IForestryPacketHandlerServer packetHandler = packetId.getPacketHandler();
		checkThreadAndEnqueue(packetHandler, data, player, player.getServerWorld());
	}

	@SubscribeEvent
	public void onPacket(ClientCustomPacketEvent event) {
		PacketBufferForestry data = new PacketBufferForestry(event.getPacket().payload());
		EntityPlayer player = Proxies.common.getPlayer();

		byte packetIdOrdinal = data.readByte();
		PacketIdClient packetId = PacketIdClient.VALUES[packetIdOrdinal];
		IForestryPacketHandlerClient packetHandler = packetId.getPacketHandler();
		checkThreadAndEnqueue(packetHandler, data, player, Minecraft.getMinecraft());
	}

	public void sendPacket(FMLProxyPacket packet, EntityPlayerMP player) {
		channel.sendTo(packet, player);
	}

	private static void checkThreadAndEnqueue(final IForestryPacketHandlerClient packet, final PacketBufferForestry data, final EntityPlayer player, IThreadListener threadListener) {
		if (!threadListener.isCallingFromMinecraftThread()) {
			threadListener.addScheduledTask(() -> {
				try {
					packet.onPacketData(data, player);
				} catch (IOException e) {
					Log.error("Network Error", e);
				}
			});
		}
	}

	private static void checkThreadAndEnqueue(final IForestryPacketHandlerServer packet, final PacketBufferForestry data, final EntityPlayerMP player, IThreadListener threadListener) {
		if (!threadListener.isCallingFromMinecraftThread()) {
			threadListener.addScheduledTask(() -> {
				try {
					packet.onPacketData(data, player);
				} catch (IOException e) {
					Log.error("Network Error", e);
				}
			});
		}
	}
}
