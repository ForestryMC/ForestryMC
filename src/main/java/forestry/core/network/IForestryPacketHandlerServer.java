package forestry.core.network;

import java.io.IOException;

import net.minecraft.server.level.ServerPlayer;

public interface IForestryPacketHandlerServer extends IForestryPacketHandler {
	void onPacketData(PacketBufferForestry data, ServerPlayer player) throws IOException;
}
