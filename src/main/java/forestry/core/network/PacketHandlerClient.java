package forestry.core.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.ICustomPacket;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

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

    public static void sendPacket(IForestryPacketServer packet) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientPlayNetHandler netHandler = minecraft.getConnection();
        if (netHandler != null) {
            Pair<PacketBuffer, Integer> packetData = packet.getPacketData();
            ICustomPacket<IPacket<?>> payload = NetworkDirection.PLAY_TO_SERVER.buildPacket(
                    packetData,
                    PacketHandlerServer.CHANNEL_ID
            );
            netHandler.sendPacket(payload.getThis());
        }
    }

}
