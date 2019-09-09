package forestry.core.network;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraftforge.fml.network.ICustomPacket;
import net.minecraftforge.fml.network.NetworkDirection;

public class Network {

	@OnlyIn(Dist.CLIENT)
	public static void sendPacketToServer(ForestryPacket packet) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientPlayNetHandler netHandler = minecraft.getConnection();
		if (netHandler != null) {
			Pair<PacketBuffer, Integer> packetData = packet.getPacketData();
			ICustomPacket<IPacket<?>> payload = NetworkDirection.PLAY_TO_SERVER.buildPacket(packetData, PacketHandlerServer.CHANNEL_ID);
			netHandler.sendPacket(payload.getThis());
		}
	}

	public static void sendPacketToClient(ForestryPacket packet, ServerPlayerEntity player) {
		Pair<PacketBuffer, Integer> packetData = packet.getPacketData();
		ICustomPacket<IPacket<?>> payload = NetworkDirection.PLAY_TO_CLIENT.buildPacket(packetData, PacketHandlerServer.CHANNEL_ID);
		player.connection.sendPacket(payload.getThis());
	}
}
