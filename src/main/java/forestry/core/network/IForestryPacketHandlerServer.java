package forestry.core.network;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;

public interface IForestryPacketHandlerServer extends IForestryPacketHandler {
	void onPacketData(PacketBufferForestry data, EntityPlayerMP player) throws IOException;
}
