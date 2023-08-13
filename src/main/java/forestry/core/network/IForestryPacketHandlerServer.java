package forestry.core.network;

import net.minecraft.server.level.ServerPlayer;

public interface IForestryPacketHandlerServer extends IForestryPacketHandler {
	void onPacketData(PacketBufferForestry data, ServerPlayer player);
}
