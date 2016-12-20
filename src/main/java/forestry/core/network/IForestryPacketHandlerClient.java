package forestry.core.network;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

public interface IForestryPacketHandlerClient extends IForestryPacketHandler {
	void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException;
}
