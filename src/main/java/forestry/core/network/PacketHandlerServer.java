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

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.network.ICustomPacket;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import forestry.core.config.Constants;

//import net.minecraft.util.IThreadListener;
//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//import net.minecraftforge.fml.common.network.FMLEventChannel;
//import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
//import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
//import net.minecraftforge.fml.common.network.NetworkRegistry;
//import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class PacketHandlerServer {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final ResourceLocation CHANNEL_ID = new ResourceLocation(Constants.MOD_ID, "channel");
    public static final String VERSION = "1.0.0";

    //	public static final String channelId = "FOR";	//TODO - change to 1 or similar...
    //	public static final EventNetworkChannel channel = NetworkRegistry.ChannelBuilder
    //			.named(new ResourceLocation(Constants.MOD_ID, "channel"))
    //			.clientAcceptedVersions(s -> s.equals("1"))
    //			.serverAcceptedVersions(s -> s.equals("1"))
    //			.networkProtocolVersion(() -> "1")
    //			.eventNetworkChannel();
    //	static {
    //		channel.addListener();
    //	}

    public PacketHandlerServer() {
        //		channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(channelId);
        //		channel.register(this);
    }

    public void onPacket(NetworkEvent.ClientCustomPayloadEvent event) {
        PacketBufferForestry data = new PacketBufferForestry(event.getPayload());
        NetworkEvent.Context ctx = event.getSource().get();
        ServerPlayerEntity player = ctx.getSender();

        if (player == null) {
            LOGGER.warn("the player was null, event: {}", event);
            return;
        }

        try {
            byte packetIdOrdinal = data.readByte();
            PacketIdServer packetId = PacketIdServer.VALUES[packetIdOrdinal];
            IForestryPacketHandlerServer packetHandler = packetId.getPacketHandler();
            packetHandler.onPacketData(data, player);
        } catch (IOException e) {    //TODO - is this actually thrown?
            LOGGER.error("exception handling packet", e);
            return;
        }
        event.getSource().get().setPacketHandled(true);
    }

    public static void sendPacket(IForestryPacketClient packet, ServerPlayerEntity player) {
        Pair<PacketBuffer, Integer> packetData = packet.getPacketData();
        ICustomPacket<IPacket<?>> payload = NetworkDirection.PLAY_TO_CLIENT.buildPacket(packetData, PacketHandlerServer.CHANNEL_ID);
        player.connection.sendPacket(payload.getThis());
    }

    //	@OnlyIn(Dist.CLIENT)
    //	private static void checkThreadAndEnqueue(final IForestryPacketHandlerClient packet, final PacketBufferForestry data, IThreadListener threadListener) {
    //		if (!threadListener.isCallingFromMinecraftThread()) {
    //			data.retain();
    //			threadListener.addScheduledTask(() -> {
    //				try {
    //					PlayerEntity player = Minecraft.getInstance().player;
    //					Preconditions.checkNotNull(player, "Tried to send data to client before the player exists.");
    //					packet.onPacketData(data, player);
    //					data.release();
    //				} catch (IOException e) {
    //					Log.error("Network Error", e);
    //				}
    //			});
    //		}
    //	}
    //
    //	private static void checkThreadAndEnqueue(final IForestryPacketHandlerServer packet, final PacketBufferForestry data, final ServerPlayerEntity player, IThreadListener threadListener) {
    //		if (!threadListener.isCallingFromMinecraftThread()) {
    //			data.retain();
    //			threadListener.addScheduledTask(() -> {
    //				try {
    //					packet.onPacketData(data, player);
    //					data.release();
    //				} catch (IOException e) {
    //					Log.error("Network Error", e);
    //				}
    //			});
    //		}
    //	}
}
