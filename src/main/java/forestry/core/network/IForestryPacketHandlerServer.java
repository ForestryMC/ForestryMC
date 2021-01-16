package forestry.core.network;

import java.io.IOException;

import net.minecraft.entity.player.ServerPlayerEntity;

public interface IForestryPacketHandlerServer extends IForestryPacketHandler {
	void onPacketData(PacketBufferForestry data, ServerPlayerEntity player) throws IOException;
}
