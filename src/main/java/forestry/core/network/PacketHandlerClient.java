package forestry.core.network;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraftforge.fml.network.NetworkEvent;

@OnlyIn(Dist.CLIENT)
public class PacketHandlerClient {

	private static final Logger LOGGER = LogManager.getLogger();

	public void onPacket(NetworkEvent.ServerCustomPayloadEvent event) {
		PacketBufferForestry data = new PacketBufferForestry(event.getPayload());
		byte idOrdinal = data.readByte();
		PacketIdClient id = PacketIdClient.VALUES[idOrdinal];

		IForestryPacketHandlerClient packetHandler = id.getPacketHandler();

		PlayerEntity player = Minecraft.getInstance().player;

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
}
