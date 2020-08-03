package forestry.core.network;

import net.minecraft.entity.player.ServerPlayerEntity;

import java.io.IOException;

public interface IForestryPacketHandlerServer extends IForestryPacketHandler {
    void onPacketData(PacketBufferForestry data, ServerPlayerEntity player) throws IOException;
}
