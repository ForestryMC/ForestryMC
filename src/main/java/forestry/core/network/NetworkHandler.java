package forestry.core.network;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.event.EventNetworkChannel;

public class NetworkHandler {

	private final EventNetworkChannel channel;

	public NetworkHandler() {
		channel = NetworkRegistry.newEventChannel(PacketHandlerServer.CHANNEL_ID, () -> PacketHandlerServer.VERSION, s -> s.equals(PacketHandlerServer.VERSION), s -> s.equals(PacketHandlerServer.VERSION));
	}

	public void serverPacketHandler() {
		PacketHandlerServer packetHandlerServer = new PacketHandlerServer();
		channel.addListener(packetHandlerServer::onPacket);
	}

	@OnlyIn(Dist.CLIENT)
	public void clientPacketHandler() {
		PacketHandlerClient packetHandlerClient = new PacketHandlerClient();
		channel.addListener(packetHandlerClient::onPacket);
	}
}
