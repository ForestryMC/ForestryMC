package forestry.core.network;

import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.ICustomPacket;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

@OnlyIn(Dist.CLIENT)
public class PacketHandlerClient {

	private static final Logger LOGGER = LogManager.getLogger();

	public void onPacket(NetworkEvent.ServerCustomPayloadEvent event) {
		PacketBufferForestry data = new PacketBufferForestry(event.getPayload());
		byte idOrdinal = data.readByte();
		PacketIdClient id = PacketIdClient.VALUES[idOrdinal];

		IForestryPacketHandlerClient packetHandler = id.getPacketHandler();

		Player player = Minecraft.getInstance().player;

		if (player == null) {
			LOGGER.warn("the player was null, event: {}", event);
			return;
		}

		try {
			packetHandler.onPacketData(data, player);

		} catch (IOException e) {
			LOGGER.error("exception handling packet", e);
			return;
		}
		event.getSource().get().setPacketHandled(true);
	}

	public static void sendPacket(IForestryPacketServer packet) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientPacketListener netHandler = minecraft.getConnection();
		if (netHandler != null) {
			Pair<FriendlyByteBuf, Integer> packetData = packet.getPacketData();
			ICustomPacket<Packet<?>> payload = NetworkDirection.PLAY_TO_SERVER.buildPacket(packetData, PacketHandlerServer.CHANNEL_ID);
			netHandler.send(payload.getThis());
		}
	}

}
