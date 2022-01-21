package forestry.core.network;

import java.io.IOException;

import net.minecraft.world.entity.player.Player;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IForestryPacketHandlerClient extends IForestryPacketHandler {
	void onPacketData(PacketBufferForestry data, Player player) throws IOException;
}
