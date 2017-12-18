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

import com.google.common.base.Preconditions;

import java.io.IOException;

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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.utils.Log;

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
		EntityPlayerMP player = ((NetHandlerPlayServer) event.getHandler()).player;

		byte packetIdOrdinal = data.readByte();
		PacketIdServer packetId = PacketIdServer.VALUES[packetIdOrdinal];
		IForestryPacketHandlerServer packetHandler = packetId.getPacketHandler();
		checkThreadAndEnqueue(packetHandler, data, player, player.getServerWorld());
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPacket(ClientCustomPacketEvent event) {
		PacketBufferForestry data = new PacketBufferForestry(event.getPacket().payload());

		byte packetIdOrdinal = data.readByte();
		PacketIdClient packetId = PacketIdClient.VALUES[packetIdOrdinal];
		IForestryPacketHandlerClient packetHandler = packetId.getPacketHandler();
		checkThreadAndEnqueue(packetHandler, data, Minecraft.getMinecraft());
	}

	public void sendPacket(FMLProxyPacket packet, EntityPlayerMP player) {
		channel.sendTo(packet, player);
	}

	@SideOnly(Side.CLIENT)
	private static void checkThreadAndEnqueue(final IForestryPacketHandlerClient packet, final PacketBufferForestry data, IThreadListener threadListener) {
		if (!threadListener.isCallingFromMinecraftThread()) {
			data.retain();
			threadListener.addScheduledTask(() -> {
				try {
					EntityPlayer player = Minecraft.getMinecraft().player;
					Preconditions.checkNotNull(player, "Tried to send data to client before the player exists.");
					packet.onPacketData(data, player);
					data.release();
				} catch (IOException e) {
					Log.error("Network Error", e);
				}
			});
		}
	}

	private static void checkThreadAndEnqueue(final IForestryPacketHandlerServer packet, final PacketBufferForestry data, final EntityPlayerMP player, IThreadListener threadListener) {
		if (!threadListener.isCallingFromMinecraftThread()) {
			data.retain();
			threadListener.addScheduledTask(() -> {
				try {
					packet.onPacketData(data, player);
					data.release();
				} catch (IOException e) {
					Log.error("Network Error", e);
				}
			});
		}
	}
}
